package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/drill-stem-test/results")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class DrillStemTestSummaryController {

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;
  private final ControllerHelperService controllerHelperService;
  private final DrillStemTestFormService drillStemTestFormService;
  private final SubmissionDetailService submissionDetailService;
  private final DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;

  public static final String PAGE_NAME = "Drill stem test results";

  @Autowired
  public DrillStemTestSummaryController(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                        DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService,
                                        ControllerHelperService controllerHelperService,
                                        DrillStemTestFormService drillStemTestFormService,
                                        SubmissionDetailService submissionDetailService,
                                        DrillStemTestResultsSummaryService drillStemTestResultsSummaryService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
    this.controllerHelperService = controllerHelperService;
    this.drillStemTestFormService = drillStemTestFormService;
    this.submissionDetailService = submissionDetailService;
    this.drillStemTestResultsSummaryService = drillStemTestResultsSummaryService;
  }

  @GetMapping
  public ModelAndView getDrillStemTestSummaryPage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var drillStemTest = drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetailOrThrow(submissionDetail);
    var drillStemTestResults = drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest);

    if (drillStemTestResults.isEmpty()) {
      return ReverseRouter.redirect(on(DrillStemTestController.class)
          .getDrillStemTestPage(submissionDetailId, null));
    } else {
      return getDrillStemTestSummaryModelAndView(
          drillStemTestFormService.getDrillStemTestSummaryForm(submissionDetail), submissionDetail);
    }
  }

  @PostMapping
  public ModelAndView saveDrillStemTestSummary(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") DrillStemTestSummaryForm form,
                                               BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestSummaryModelAndView(form, submissionDetail),
        () -> {
          drillStemTestInitialPersistenceService.saveDrillStemTest(form, submissionDetail);
          if (form.getDrillStemTestSummaryOption() == DrillStemTestSummaryOptions.YES_NOW) {
            return ReverseRouter.redirect(on(DrillStemTestResultController.class)
                .getCreateNewDrillStemTestResultPage(submissionDetailId));
          }
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getDrillStemTestSummaryModelAndView(DrillStemTestSummaryForm form, SubmissionDetail submissionDetail) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestSummary")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), null, null)))
        .addObject("drillStemTestSummaryOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(DrillStemTestSummaryOptions.class))
        .addObject("drillStemTestResultSummaryList", drillStemTestResultsSummaryService
            .getDrillStemTestResultSummaryViewsFromSubmissionDetail(submissionDetail));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }
}
