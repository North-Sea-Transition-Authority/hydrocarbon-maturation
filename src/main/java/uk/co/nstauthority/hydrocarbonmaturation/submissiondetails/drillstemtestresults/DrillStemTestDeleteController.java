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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AllowedSubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/drill-stem-test/{drillStemTestResultId}")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.DISCOVERY_TYPES)
@IsFromResponsibleTeam
class DrillStemTestDeleteController {

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;
  private final SubmissionDetailService submissionDetailService;

  public static final String PAGE_NAME = "Delete confirmation";

  @Autowired
  DrillStemTestDeleteController(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                DrillStemTestResultsSummaryService drillStemTestResultsSummaryService,
                                SubmissionDetailService submissionDetailService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestResultsSummaryService = drillStemTestResultsSummaryService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping("/delete-confirmation")
  public ModelAndView getDrillStemTestResultDeleteConfirmationPage(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("drillStemTestResultId") UUID drillStemTestResultId) {
    var drillStemTestResult =
        drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    return getDrillStemTestResultDeleteConfirmationModelAndView(submissionDetailId, drillStemTestResult);
  }

  @PostMapping("/delete-drill-stem-test-result")
  public ModelAndView deleteDrillStemTestResult(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("drillStemTestResultId") UUID drillStemTestResultId,
      RedirectAttributes redirectAttributes) {
    var drillStemTestResult = drillStemTestResultPersistenceService
        .getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    addRedirectNotification(drillStemTestResult.getFormationName(), redirectAttributes);

    drillStemTestResultPersistenceService.deleteDrillStemTestResult(drillStemTestResult);

    var drillStemTest = drillStemTestResultPersistenceService.getDrillStemTestBySubmissionDetailOrThrow(submissionDetail);
    var drillStemTestResults = drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest);
    if (drillStemTestResults.isEmpty()) {
      return ReverseRouter.redirect(on(DrillStemTestController.class).getDrillStemTestPage(submissionDetailId, null));
    } else {
      return ReverseRouter.redirect(
          on(DrillStemTestSummaryController.class).getDrillStemTestSummaryPage(submissionDetailId));
    }
  }

  private ModelAndView getDrillStemTestResultDeleteConfirmationModelAndView(UUID submissionDetailId,
                                                                            DrillStemTestResult drillStemTestResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var modelAndView = new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestResultDeleteConfirmation")
        .addObject("backToSummaryUrl", ReverseRouter.route(on(DrillStemTestSummaryController.class)
            .getDrillStemTestSummaryPage(submissionDetailId)))
        .addObject("actionUrl", ReverseRouter.route(on(DrillStemTestDeleteController.class)
            .deleteDrillStemTestResult(submissionDetailId, drillStemTestResult.getId(), null)))
        .addObject("drillStemTestResultSummaryView",
            drillStemTestResultsSummaryService.createSummaryViewFromDrillStemTestResult(
                drillStemTestResult, DrillStemTestResultSummaryMode.VIEW, 1));

    var breadCrumbs = getBreadcrumbs(submissionDetail);
    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadCrumbs);
    return modelAndView;
  }

  private Breadcrumbs getBreadcrumbs(SubmissionDetail submissionDetail) {
    return Breadcrumbs.builder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(new BreadcrumbItem(
            TaskListController.PAGE_NAME,
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId()))))
        .addBreadcrumb(new BreadcrumbItem(
            DrillStemTestController.PAGE_NAME,
            ReverseRouter.route(on(DrillStemTestController.class)
                .getDrillStemTestPage(submissionDetail.getId(), null))))
        .build();
  }

  private void addRedirectNotification(String formationName, RedirectAttributes redirectAttributes) {
    if (formationName == null) {
      NotificationBannerUtil.addSuccessNotification(redirectAttributes, "Drill stem test result deleted",
          "The drill stem test result has been deleted.");
    } else {
      NotificationBannerUtil.addSuccessNotification(redirectAttributes, "Drill stem test result deleted",
          "The drill stem test result with formation name %s has been deleted.".formatted(
              formationName));
    }
  }
}
