package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/reservoir-submission-details/{reservoirSubmissionDetailId}/well-test")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class DrillStemTestController {

  private final DrillStemTestFormService drillStemTestFormService;
  private final ControllerHelperService controllerHelperService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;
  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Well test results";

  @Autowired
  public DrillStemTestController(DrillStemTestFormService drillStemTestFormService,
                                 ControllerHelperService controllerHelperService,
                                 DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService,
                                 DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                 ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.drillStemTestFormService = drillStemTestFormService;
    this.controllerHelperService = controllerHelperService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getDrillStemTestPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                           @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                           @ModelAttribute("form") DrillStemTestInitialForm form) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    if (drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(
        reservoirSubmissionDetail).isEmpty()) {
      return getDrillStemTestModelAndView(
          drillStemTestFormService.getDrillStemTestInitialForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
    } else {
      return ReverseRouter.redirect(on(DrillStemTestSummaryController.class)
          .getDrillStemTestSummaryPage(submissionDetailId, reservoirSubmissionDetailId));
    }
  }

  @PostMapping
  public ModelAndView saveDrillStemTestDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                               @ModelAttribute("form") DrillStemTestInitialForm form,
                                               BindingResult bindingResult) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    bindingResult = drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestModelAndView(form, reservoirSubmissionDetail),
        () -> {
          drillStemTestInitialPersistenceService.saveInitialDrillStemTest(form, reservoirSubmissionDetail);
          if (BooleanUtils.isTrue(form.getHasDrillStemTestResult())) {
            return ReverseRouter.redirect(on(DrillStemTestResultController.class)
                .getCreateNewDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId));
          }
          return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, null));
        }
    );
  }

  private ModelAndView getDrillStemTestModelAndView(DrillStemTestInitialForm form,
                                                    ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var modelAndView =  new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestInitial")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null, null)));

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(
        modelAndView, PAGE_NAME, reservoirSubmissionDetail);
    return modelAndView;
  }
}
