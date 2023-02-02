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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/drill-stem-test")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class DrillStemTestResultController {

  private final DrillStemTestResultFormService drillStemTestResultFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  public static final String PAGE_NAME = "Drill stem test result";

  @Autowired
  public DrillStemTestResultController(DrillStemTestResultFormService drillStemTestResultFormService,
                                       ControllerHelperService controllerHelperService,
                                       SubmissionDetailService submissionDetailService,
                                       DrillStemTestResultPersistenceService drillStemTestResultPersistenceService) {
    this.drillStemTestResultFormService = drillStemTestResultFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
  }

  @GetMapping("/{drillStemTestResultId}")
  public ModelAndView getDrillStemTestResultPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                 @PathVariable("drillStemTestResultId") UUID drillStemTestResultId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var drillStemTestResult = drillStemTestResultPersistenceService
        .getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    return getDrillStemTestResultModelAndView(
        drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult),
        submissionDetail);
  }

  @GetMapping("/create-new")
  public ModelAndView getCreateNewDrillStemTestResultPage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getDrillStemTestResultModelAndView(
        new DrillStemTestResultForm(),
        submissionDetail);
  }

  @PostMapping("/create-new")
  public ModelAndView saveNewDrillStemTestResult(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                 @ModelAttribute("form") DrillStemTestResultForm form,
                                                 BindingResult bindingResult,
                                                 ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var drillStemTest = drillStemTestResultPersistenceService
        .getDrillStemTestBySubmissionDetailOrThrow(submissionDetail);
    var drillStemTestResults = drillStemTestResultPersistenceService
        .getDrillStemTestResultsByDrillStemTest(drillStemTest);
    bindingResult = drillStemTestResultFormService.validate(
        form, bindingResult,
        drillStemTestResultFormService.getDrillStemTestResultNumbers(drillStemTestResults));
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestResultModelAndView(form, submissionDetail),
        () -> {
          var drillStemTestResult = drillStemTestResultPersistenceService
              .createNewDrillStemTestResult(drillStemTest);
          drillStemTestResultPersistenceService.saveDrillStemTestResult(
              form,
              drillStemTest,
              drillStemTestResult.getId());
          return ReverseRouter.redirect(on(DrillStemTestSummaryController.class)
              .getDrillStemTestSummaryPage(submissionDetailId));
        }
    );
  }

  @PostMapping("/{drillStemTestResultId}")
  public ModelAndView updateDrillStemTestResultDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                       @PathVariable("drillStemTestResultId") UUID drillStemTestResultId,
                                                       @ModelAttribute("form") DrillStemTestResultForm form,
                                                       BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var drillStemTestResult = drillStemTestResultPersistenceService
        .getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    var drillStemTestResults = drillStemTestResultPersistenceService
        .getDrillStemTestResultsByDrillStemTest(drillStemTestResult.getDrillStemTest());
    bindingResult = drillStemTestResultFormService.validateForUpdate(form, bindingResult,
        drillStemTestResults, drillStemTestResult.getTestNumber());
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestResultModelAndView(form, submissionDetail),
        () -> {
          drillStemTestResultPersistenceService.saveDrillStemTestResult(
              form,
              drillStemTestResult.getDrillStemTest(),
              drillStemTestResultId);
          return ReverseRouter.redirect(on(DrillStemTestSummaryController.class)
              .getDrillStemTestSummaryPage(submissionDetailId));
        }
    );
  }

  private ModelAndView getDrillStemTestResultModelAndView(DrillStemTestResultForm form,
                                                          SubmissionDetail submissionDetail) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestResult")
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(DrillStemTestSummaryController.class)
            .getDrillStemTestSummaryPage(submissionDetail.getId())));

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
}
