package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummary;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DeleteReservoirController.class)
class DeleteReservoirControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/reservoir/deleteReservoir";
  public static final String RESERVOIR_NAME = "reservoir name";

  @MockBean
  private ReservoirSummaryService reservoirSummaryService;

  @MockBean
  private DeleteReservoirService deleteReservoirService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private SubmissionDetail submissionDetail;
  private ReservoirSummary reservoirSummary;

  @BeforeEach
  void setUp() {
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME);
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir);
    submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionDetail(submissionDetail.getId())).thenReturn(Optional.of(submissionDetail));
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    reservoirSummary = new ReservoirSummary(RESERVOIR_NAME, Collections.emptyList());
    when(reservoirSummaryService.getReservoirSummaryForIndividualReservoir(reservoirSubmissionDetail))
        .thenReturn(reservoirSummary);
  }

  @SecurityTest
  void getDeleteReservoirPage_assertStatusOk() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteReservoirController.class).getDeleteReservoirPage(
            submissionDetail.getId(),
            reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getDeleteReservoirPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteReservoirController.class).getDeleteReservoirPage(
            submissionDetail.getId(),
            reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDeleteReservoirPage_andStatusIsProspect_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteReservoirController.class).getDeleteReservoirPage(
            submissionDetail.getId(),
            reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDeleteReservoirPage_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER, submissionDetail);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteReservoirController.class).getDeleteReservoirPage(
            submissionDetail.getId(),
            reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getDeleteReservoirPage_assertModelProperties_canDeleteReservoir() throws Exception {
    when(deleteReservoirService.canDeleteReservoir(reservoirSubmissionDetail)).thenReturn(true);
    var modelMap =
        Objects.requireNonNull(
            mockMvc.perform(get(ReverseRouter.route(on(DeleteReservoirController.class).getDeleteReservoirPage(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
                    .with(user(USER)))
                .andReturn()
                .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName", DeleteReservoirController.PAGE_NAME),
            entry("reservoirName", RESERVOIR_NAME),
            entry("summary", reservoirSummary),
            entry("deleteUrl", ReverseRouter.route(on(DeleteReservoirController.class)
                .deleteReservoir(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null))),
            entry("cancelUrl", ReverseRouter.route(on(ReservoirTaskListController.class)
                .getReservoirTaskList(submissionDetail.getId(), null))),
            entry("canDeleteReservoir", true)
        )
        .doesNotContainKey("associatedFields");

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetail.getId(), null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @Test
  void getDeleteReservoirPage_assertModelProperties_cantDeleteReservoir() throws Exception {
    when(deleteReservoirService.canDeleteReservoir(reservoirSubmissionDetail)).thenReturn(false);
    var fieldNames = List.of("field1", "field2");
    when(deleteReservoirService.getLinkedFieldNamesForDiscoveryReservoir(reservoirSubmissionDetail)).thenReturn(fieldNames);
    var modelMap =
        Objects.requireNonNull(
            mockMvc.perform(get(ReverseRouter.route(on(DeleteReservoirController.class).getDeleteReservoirPage(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId())))
                    .with(user(USER)))
                .andReturn()
                .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName", DeleteReservoirController.PAGE_NAME),
            entry("reservoirName", RESERVOIR_NAME),
            entry("summary", reservoirSummary),
            entry("deleteUrl", ReverseRouter.route(on(DeleteReservoirController.class)
                .deleteReservoir(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null))),
            entry("cancelUrl", ReverseRouter.route(on(ReservoirTaskListController.class)
                .getReservoirTaskList(submissionDetail.getId(), null))),
            entry("canDeleteReservoir", false),
            entry("associatedFields", fieldNames)
        );

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetail.getId(), null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @Test
  void deleteReservoir_assertRedirection() throws Exception {
    var flashMap = mockMvc.perform(
            post(ReverseRouter.route(on(DeleteReservoirController.class)
                .deleteReservoir(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetail.getId(), null))))
        .andReturn().getFlashMap();

    verify(deleteReservoirService, times(1)).deleteReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat((NotificationBanner) flashMap.get("flash"))
        .extracting(
            NotificationBanner::getTitle,
            NotificationBanner::getContent)
        .containsExactly(
            "Reservoir deleted",
            "%s has been deleted".formatted(reservoirSubmissionDetail.getReservoir().getName()));
  }
}
