package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ReservoirTaskListController.class)
class ReservoirTaskListControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ReservoirTaskListService reservoirTaskListService;

  @SecurityTest
  void getReservoirTaskList_forDiscovery() throws Exception {
    setUpWhenUserIsResponsibleTeam_passSecurity();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var taskListItems = List.of(
        new TaskListItem("item1", "actionUrl1"),
        new TaskListItem("item2", "actionUrl2")
    );

    var taskLists = List.of(
        new ReservoirTaskListItem("reservoir 1", taskListItems),
        new ReservoirTaskListItem("reservoir 2", taskListItems)
    );

    when(reservoirTaskListService.getAllReservoirTaskLists(submissionDetail)).thenReturn(taskLists);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirTaskListController.class)
                        .getReservoirTaskList(submissionDetail.getId(), null)))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/submissiondetails/tasklist/reservoirTaskList"))
                .andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("reservoirTaskListItems", taskLists),
            entry("pageTitle", ReservoirTaskListController.PAGE_TITLE)
        );

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME)
    );
  }

  @SecurityTest
  void getTaskList_whenNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirTaskListController.class)
                .getReservoirTaskList(submissionDetail.getId(), null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}
