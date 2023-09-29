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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/well-test/results")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_TYPES)
@IsFromResponsibleTeam
public class DrillStemTestSummaryController {

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;
  private final ControllerHelperService controllerHelperService;
  private final DrillStemTestFormService drillStemTestFormService;
  private final SubmissionDetailService submissionDetailService;
  private final DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Well test results";

  @Autowired
  public DrillStemTestSummaryController(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                        DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService,
                                        ControllerHelperService controllerHelperService,
                                        DrillStemTestFormService drillStemTestFormService,
                                        SubmissionDetailService submissionDetailService,
                                        DrillStemTestResultsSummaryService drillStemTestResultsSummaryService,
                                        ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
    this.controllerHelperService = controllerHelperService;
    this.drillStemTestFormService = drillStemTestFormService;
    this.submissionDetailService = submissionDetailService;
    this.drillStemTestResultsSummaryService = drillStemTestResultsSummaryService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getDrillStemTestSummaryPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                  @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    
    var drillStemTest = drillStemTestInitialPersistenceService
        .getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail);
    var drillStemTestResults = drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest);

    if (drillStemTestResults.isEmpty()) {
      return ReverseRouter.redirect(on(DrillStemTestController.class)
          .getDrillStemTestPage(submissionDetailId, reservoirSubmissionDetailId, null));
    } else {
      return getDrillStemTestSummaryModelAndView(
          drillStemTestFormService.getDrillStemTestSummaryForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
    }
  }

  @PostMapping
  public ModelAndView saveDrillStemTestSummary(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                               @ModelAttribute("form") DrillStemTestSummaryForm form,
                                               BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    bindingResult = drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestSummaryModelAndView(form, reservoirSubmissionDetail),
        () -> {
          drillStemTestInitialPersistenceService.saveDrillStemTest(form, reservoirSubmissionDetail);
          if (form.getDrillStemTestSummaryOption() == DrillStemTestSummaryOptions.YES_NOW) {
            return ReverseRouter.redirect(on(DrillStemTestResultController.class)
                .getCreateNewDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId));
          }
          return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, null));
        }
    );
  }

  private ModelAndView getDrillStemTestSummaryModelAndView(DrillStemTestSummaryForm form,
                                                           ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var modelAndView =  new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestSummary")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null, null)))
        .addObject("drillStemTestSummaryOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(DrillStemTestSummaryOptions.class))
        .addObject("drillStemTestResultSummaryList", drillStemTestResultsSummaryService
            .getDrillStemTestResultSummaryViewsFromReservoirSubmissionDetail(reservoirSubmissionDetail));

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(
        modelAndView, PAGE_NAME, reservoirSubmissionDetail);
    return modelAndView;
  }
}
