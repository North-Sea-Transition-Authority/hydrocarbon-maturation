package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/summary")
public class ProspectSubmissionSummaryController {

  private final ProspectSummaryService prospectSummaryService;
  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final ProspectSubmissionService prospectSubmissionService;
  private final SubmissionSummaryFormService submissionSummaryFormService;

  public static final String PAGE_NAME = "Check your answers before submitting";

  @Autowired
  public ProspectSubmissionSummaryController(ProspectSummaryService prospectSummaryService,
                                             SubmissionDetailService submissionDetailService,
                                             ControllerHelperService controllerHelperService,
                                             HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                             ProspectSubmissionService prospectSubmissionService,
                                             SubmissionSummaryFormService submissionSummaryFormService) {
    this.prospectSummaryService = prospectSummaryService;
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.prospectSubmissionService = prospectSubmissionService;
    this.submissionSummaryFormService = submissionSummaryFormService;
  }

  @GetMapping
  public ModelAndView showSubmissionSummary(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") SubmissionSummaryForm submissionSummaryForm) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getSubmissionSummaryModelAndView(submissionSummaryForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView processSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") SubmissionSummaryForm form,
                                        BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = submissionSummaryFormService.validate(form, bindingResult, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubmissionSummaryModelAndView(form, submissionDetail),
        () -> {
          submissionDetailService.completeSubmission(submissionDetail);
          return ReverseRouter.redirect(on(SubmissionConfirmationController.class)
              .showSubmissionConfirmation(submissionDetailId));
        }
    );
  }

  private ModelAndView getSubmissionSummaryModelAndView(SubmissionSummaryForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/prospectSubmissionSummary")
        .addObject("form", form)
        .addObject("generalDetailsSummaryView",
            prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("chanceOfSuccessSummaryView",
            prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("hydrocarbonCaseSummaryView",
            prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            prospectSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("trapPropertiesSummaryView",
            prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseVolumetricsSummaryView",
            prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseOutputsSummaryView",
            prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseVolumetricsSummaryView",
            prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseOutputsSummaryView",
            prospectSummaryService.getGasCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseVolumetricsSummaryView",
            prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseOutputsSummaryView",
            prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("opportunityDetailsSummaryView",
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("submittable", prospectSubmissionService.isSubmissionSubmittable(submissionDetail))
        .addObject("gasCase", hydrocarbonCasePersistenceService.isGasOnlyPresent(submissionDetail))
        .addObject("oilCase", hydrocarbonCasePersistenceService.isOilOnlyPresent(submissionDetail))
        .addObject("mixedCase", hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail))
        .addObject("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}
