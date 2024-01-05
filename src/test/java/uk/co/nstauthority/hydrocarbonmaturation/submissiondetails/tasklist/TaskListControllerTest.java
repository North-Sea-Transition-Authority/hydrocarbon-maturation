package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.DeleteSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdate;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = TaskListController.class)
class TaskListControllerTest extends AbstractSubmissionControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();
  private static final String SUBMISSION_UPDATE_REASON = "submission update reason here";
  @MockBean
  private TaskListService taskListService;

  @MockBean
  private SubmissionUpdateService submissionUpdateService;

  @MockBean
  private SubmissionResourceService submissionResourceService;

  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();

  }

  @SecurityTest
  void getTaskList() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    var prospectName = "pros123";
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn(prospectName);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)).thenReturn(submissionDetail.getSubmissionType().getTypeName());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    var taskListSections = List.of(
        new TaskListSection("Reservoir", 10, List.of(
            new TaskListItem("item1", "actionUrl1"),
            new TaskListItem("item2", "actionUrl2")
        )),
        new TaskListSection("Section2", 10, List.of(
            new TaskListItem("item1s2", "actionUrl1s2")))
    );

    when(taskListService.getAllSections(submissionDetail))
        .thenReturn(List.of(
            new TaskListSection(
                "Reservoir",
                10,
                List.of(
                    new TaskListItem("item1", "actionUrl1"),
                    new TaskListItem("item2", "actionUrl2")
                )),
                new TaskListSection(
                    "Section2",
                    10,
                    List.of(
                        new TaskListItem("item1s2", "actionUrl1s2")
                    )))
        );

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submissionDetailId)))
                .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/submissiondetails/tasklist/taskList"))
            .andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("taskListSections", taskListSections),
            entry("resourceName", prospectName),
            entry("deleteSubmissionUrl", ReverseRouter.route(on(DeleteSubmissionController.class)
                .showDeleteConfirmationPage(submissionDetailId))),
            entry("isSubmissionUpdate", false),
            entry("deleteLinkText", "Delete submission")
        )
        .doesNotContainKey("submissionUpdateReason");

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE)
    );
  }

  @SecurityTest
  void getTaskList_whenNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getTaskList_whenVersionIsGreaterThanOneAndUpdatePresent_assertIsUpdateAndReason() throws Exception {
    submissionDetail.setVersionNumber(2);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)).thenReturn(submissionDetail.getSubmissionType().getTypeName());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    when(taskListService.getAllSections(submissionDetail))
        .thenReturn(List.of());

    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setReason(SUBMISSION_UPDATE_REASON);
    when(submissionUpdateService.getLatestUnresolvedUpdateForUpdateSubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(submissionUpdate));

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(TaskListController.class)
                        .getTaskList(submissionDetailId)))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/submissiondetails/tasklist/taskList"))
                .andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionUpdateReason", SUBMISSION_UPDATE_REASON),
            entry("isSubmissionUpdate", true),
            entry("deleteLinkText", "Delete submission update")
        );
  }

  @Test
  void getTaskList_whenVersionIsGreaterThanOneAndUpdateNotPresent_assertIsNotUpdateAndNoReason() throws Exception {
    submissionDetail.setVersionNumber(2);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)).thenReturn(submissionDetail.getSubmissionType().getTypeName());

    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    when(submissionUpdateService.getLatestUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(Optional.empty());

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(TaskListController.class)
                        .getTaskList(submissionDetailId)))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/submissiondetails/tasklist/taskList"))
                .andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("isSubmissionUpdate", false),
            entry("deleteLinkText", "Delete submission update")
        )
        .doesNotContainKey("submissionUpdateReason");
  }

  @Test
  void getTaskList_whenCorrect_assertDeleteLinkText() throws Exception {
    submissionDetail.setCorrection(true);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)).thenReturn(submissionDetail.getSubmissionType().getTypeName());

    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    when(submissionUpdateService.getLatestUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(Optional.empty());

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(TaskListController.class)
                        .getTaskList(submissionDetailId)))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/submissiondetails/tasklist/taskList"))
                .andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("deleteLinkText", "Delete correction")
        );
  }
}