package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/well-test/{drillStemTestResultId}")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
class DrillStemTestDeleteController {

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Delete confirmation";

  @Autowired
  DrillStemTestDeleteController(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                DrillStemTestResultsSummaryService drillStemTestResultsSummaryService,
                                ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestResultsSummaryService = drillStemTestResultsSummaryService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping("/delete-confirmation")
  public ModelAndView getDrillStemTestResultDeleteConfirmationPage(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
      @PathVariable("drillStemTestResultId") UUID drillStemTestResultId) {
    var drillStemTestResult =
        drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    return getDrillStemTestResultDeleteConfirmationModelAndView(reservoirSubmissionDetailId, drillStemTestResult);
  }

  @PostMapping("/delete-well-test-result")
  public ModelAndView deleteDrillStemTestResult(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
      @PathVariable("drillStemTestResultId") UUID drillStemTestResultId,
      RedirectAttributes redirectAttributes) {
    var drillStemTestResult = drillStemTestResultPersistenceService
        .getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    addRedirectNotification(drillStemTestResult.getFormationName(), redirectAttributes);

    drillStemTestResultPersistenceService.deleteDrillStemTestResult(drillStemTestResult);

    var drillStemTest = drillStemTestResultPersistenceService
        .getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail);
    var drillStemTestResults = drillStemTestResultPersistenceService
        .getDrillStemTestResultsByDrillStemTest(drillStemTest);
    if (drillStemTestResults.isEmpty()) {
      return ReverseRouter.redirect(on(DrillStemTestController.class)
          .getDrillStemTestPage(submissionDetailId, reservoirSubmissionDetailId, null));
    } else {
      return ReverseRouter.redirect(
          on(DrillStemTestSummaryController.class)
              .getDrillStemTestSummaryPage(submissionDetailId, reservoirSubmissionDetailId));
    }
  }

  private ModelAndView getDrillStemTestResultDeleteConfirmationModelAndView(UUID reservoirSubmissionDetailId,
                                                                            DrillStemTestResult drillStemTestResult) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var modelAndView = new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestResultDeleteConfirmation")
        .addObject("backToSummaryUrl", ReverseRouter.route(on(DrillStemTestSummaryController.class)
            .getDrillStemTestSummaryPage(submissionDetail.getId(), reservoirSubmissionDetailId)))
        .addObject("actionUrl", ReverseRouter.route(on(DrillStemTestDeleteController.class)
            .deleteDrillStemTestResult(submissionDetail.getId(), reservoirSubmissionDetailId, drillStemTestResult.getId(), null)))
        .addObject("drillStemTestResultSummaryView",
            drillStemTestResultsSummaryService.createSummaryViewFromDrillStemTestResult(
                drillStemTestResult, DrillStemTestResultSummaryMode.VIEW, 1));

    var breadCrumbs = getBreadcrumbs(reservoirSubmissionDetail);
    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadCrumbs);
    return modelAndView;
  }

  private Breadcrumbs getBreadcrumbs(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return Breadcrumbs.builder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(new BreadcrumbItem(
            TaskListController.PAGE_NAME,
            ReverseRouter.route(on(TaskListController.class).getTaskList(reservoirSubmissionDetail.getId()))))
        .addBreadcrumb(new BreadcrumbItem(
            DrillStemTestController.PAGE_NAME,
            ReverseRouter.route(on(DrillStemTestController.class)
                .getDrillStemTestPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null))))
        .build();
  }

  private void addRedirectNotification(String formationName, RedirectAttributes redirectAttributes) {
    if (formationName == null) {
      NotificationBannerUtil.addSuccessNotification(redirectAttributes, "Well test result deleted",
          "The well test result has been deleted.");
    } else {
      NotificationBannerUtil.addSuccessNotification(redirectAttributes, "Well test result deleted",
          "The well test result with formation name %s has been deleted.".formatted(
              formationName));
    }
  }
}
