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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/reservoir-submission-details/{reservoirSubmissionDetailId}/well-test")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class DrillStemTestResultController {

  private final DrillStemTestResultFormService drillStemTestResultFormService;
  private final ControllerHelperService controllerHelperService;
  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Well test results";
  public static final String BREADCRUMB = "Results";

  @Autowired
  public DrillStemTestResultController(DrillStemTestResultFormService drillStemTestResultFormService,
                                       ControllerHelperService controllerHelperService,
                                       DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                       ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.drillStemTestResultFormService = drillStemTestResultFormService;
    this.controllerHelperService = controllerHelperService;
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping("/{drillStemTestResultId}")
  public ModelAndView getDrillStemTestResultPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                 @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                                 @PathVariable("drillStemTestResultId") UUID drillStemTestResultId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var drillStemTestResult = drillStemTestResultPersistenceService
        .getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    return getDrillStemTestResultModelAndView(
        drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult),
        reservoirSubmissionDetail);
  }

  @GetMapping("/create-new")
  public ModelAndView getCreateNewDrillStemTestResultPage(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {

    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    return getDrillStemTestResultModelAndView(
        new DrillStemTestResultForm(),
        reservoirSubmissionDetail);
  }

  @PostMapping("/create-new")
  public ModelAndView saveNewDrillStemTestResult(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                 @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                                 @ModelAttribute("form") DrillStemTestResultForm form,
                                                 BindingResult bindingResult) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    var drillStemTest = drillStemTestResultPersistenceService
        .getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail);
    var drillStemTestResults = drillStemTestResultPersistenceService
        .getDrillStemTestResultsByDrillStemTest(drillStemTest);
    bindingResult = drillStemTestResultFormService.validate(
        form,
        bindingResult,
        drillStemTestResultFormService.getDrillStemTestResultNumbers(drillStemTestResults));
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestResultModelAndView(form, reservoirSubmissionDetail),
        () -> {
          var drillStemTestResult = drillStemTestResultPersistenceService
              .createNewDrillStemTestResult(drillStemTest);
          drillStemTestResultPersistenceService.saveDrillStemTestResult(
              form,
              drillStemTest,
              drillStemTestResult.getId());
          return ReverseRouter.redirect(on(DrillStemTestSummaryController.class)
              .getDrillStemTestSummaryPage(submissionDetailId, reservoirSubmissionDetailId));
        }
    );
  }

  @PostMapping("/{drillStemTestResultId}")
  public ModelAndView updateDrillStemTestResultDetails(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
      @PathVariable("drillStemTestResultId") UUID drillStemTestResultId,
      @ModelAttribute("form") DrillStemTestResultForm form,
      BindingResult bindingResult) {


    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var drillStemTestResult = drillStemTestResultPersistenceService
        .getDrillStemTestResultByIdOrThrow(drillStemTestResultId);
    var drillStemTestResults = drillStemTestResultPersistenceService
        .getDrillStemTestResultsByDrillStemTest(drillStemTestResult.getDrillStemTest());
    bindingResult = drillStemTestResultFormService.validateForUpdate(form, bindingResult,
        drillStemTestResults, drillStemTestResult.getTestNumber());
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestResultModelAndView(form, reservoirSubmissionDetail),
        () -> {
          drillStemTestResultPersistenceService.saveDrillStemTestResult(
              form,
              drillStemTestResult.getDrillStemTest(),
              drillStemTestResultId);
          return ReverseRouter.redirect(on(DrillStemTestSummaryController.class)
              .getDrillStemTestSummaryPage(submissionDetailId, reservoirSubmissionDetailId));
        }
    );
  }

  private ModelAndView getDrillStemTestResultModelAndView(DrillStemTestResultForm form,
                                                          ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var modelAndView =  new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestResult")
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(DrillStemTestSummaryController.class)
            .getDrillStemTestSummaryPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())));

    var wellTestResultsBreadcrumb = new BreadcrumbItem(
        ReservoirTaskListService.WELL_TEST_RESULTS,
        ReverseRouter.route(on(DrillStemTestController.class)
            .getDrillStemTestPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null))
    );
    var breadcrumbs = Breadcrumbs.builder(BREADCRUMB)
        .addWorkAreaBreadcrumb()
        .addTaskListBreadcrumb(submissionDetail)
        .addReservoirTaskListBreadcrumb(submissionDetail)
        .addBreadcrumb(wellTestResultsBreadcrumb)
        .build();
    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}
