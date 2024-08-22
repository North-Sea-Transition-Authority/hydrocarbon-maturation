package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NotificationBannerTestUtil.notificationBanner;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListService;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DrillStemTestDeleteController.class)
class DrillStemTestDeleteControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @MockBean
  private DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;
  private DrillStemTest drillStemTest;
  private UUID drillStemTestResultId;

  @BeforeEach
  void setUp() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    drillStemTest = new DrillStemTest();
    drillStemTestResultId = UUID.randomUUID();
  }

  @SecurityTest
  void getDrillStemTestResultDeleteConfirmationPage_assertStatusOk() throws Exception {
    var drillStemTestResult = mock(DrillStemTestResult.class);

    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestResultId))
        .thenReturn(drillStemTestResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(drillStemTestResultsSummaryService.createSummaryViewFromDrillStemTestResult(drillStemTestResult, DrillStemTestResultSummaryMode.VIEW, 1))
        .thenReturn(DrillStemTestResultSummaryView.empty(1, "", ""));
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResultId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/drillstemtest/drillStemTestResultDeleteConfirmation"));
  }

  @Test
  void getDrillStemTestResultDeleteConfirmationPage_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    var drillStemTestResult = mock(DrillStemTestResult.class);
    var summaryView = DrillStemTestResultSummaryView.empty(1, "url", "url");
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestResultId))
        .thenReturn(drillStemTestResult);
    when(drillStemTestResultsSummaryService.createSummaryViewFromDrillStemTestResult(drillStemTestResult, DrillStemTestResultSummaryMode.VIEW, 1))
        .thenReturn(summaryView);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestDeleteController.class)
                    .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(),
                        drillStemTestResultId)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView())
        .getModel();

    assertThat(modelMap)
        .contains(
            entry("actionUrl", ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .deleteDrillStemTestResult(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResult.getId(), null))),
            entry("backToSummaryUrl", ReverseRouter.route(on(DrillStemTestSummaryController.class)
                .getDrillStemTestSummaryPage(submissionDetail.getId(), reservoirSubmissionDetail.getId()))),
            entry("drillStemTestResultSummaryView", summaryView)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class)
            .getWorkArea(null)),
            WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId(), null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null)),
            ReservoirTaskListController.PAGE_TITLE),
        entry(ReverseRouter.route(on(DrillStemTestController.class)
                .getDrillStemTestPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null)),
            ReservoirTaskListService.WELL_TEST_RESULTS)
    );
  }

  @SecurityTest
  void deleteDrillStemTestResult_lastResult_assertRedirectToInitialPage() throws Exception {
    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setFormationName("The name");

    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestResultId))
        .thenReturn(drillStemTestResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .thenReturn(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of());
    setUpWhenUserIsResponsibleTeam_passSecurity();

    var expectedBannerImplementation = NotificationBanner.builder()
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent("The well test result with formation name The name has been deleted.")
        .withTitle("Well test result deleted")
        .build();
    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .deleteDrillStemTestResult(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResultId, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            redirectedUrl(ReverseRouter.route(on(DrillStemTestController.class)
                .getDrillStemTestPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null))))
        .andExpect(notificationBanner(expectedBannerImplementation));

    verify(drillStemTestResultPersistenceService).deleteDrillStemTestResult(drillStemTestResult);
  }

  @SecurityTest
  void getDrillStemTestResultDeleteConfirmationPage_whenNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResultId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
    verifyNoInteractions(drillStemTestResultPersistenceService);
  }

  @SecurityTest
  void deleteDrillStemTestResult_whenNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .deleteDrillStemTestResult(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResultId, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isForbidden());

    verifyNoInteractions(drillStemTestResultPersistenceService);
  }

  @Test
  void deleteDrillStemTestResult_lastResult_assertRedirectToSummaryPage() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    var drillStemTestResult = new DrillStemTestResult();

    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestResultId))
        .thenReturn(drillStemTestResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when( reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .thenReturn(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of(drillStemTestResult));

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .deleteDrillStemTestResult(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResultId, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            redirectedUrl(ReverseRouter.route(on(DrillStemTestSummaryController.class)
                .getDrillStemTestSummaryPage(submissionDetail.getId(), reservoirSubmissionDetail.getId()))));

    verify(drillStemTestResultPersistenceService).deleteDrillStemTestResult(drillStemTestResult);
  }
}