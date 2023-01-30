package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import javax.validation.Valid;
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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/mixed-case-outputs")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class MixedCaseOutputsController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final MixedCaseOutputsFormService mixedCaseOutputsFormService;
  private final MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  public static final String PAGE_NAME = "Mixed case outputs (resources)";

  @Autowired
  public MixedCaseOutputsController(SubmissionDetailService submissionDetailService,
                                  ControllerHelperService controllerHelperService,
                                  ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                  MixedCaseOutputsFormService mixedCaseOutputsFormService,
                                  MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.mixedCaseOutputsFormService = mixedCaseOutputsFormService;
    this.mixedCaseOutputsPersistenceService = mixedCaseOutputsPersistenceService;
  }

  @GetMapping
  public ModelAndView getMixedCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") MixedCaseOutputsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getMixedCaseOutputsModelAndView(
        mixedCaseOutputsFormService.getMixedCaseOutputsForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveMixedCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @Valid @ModelAttribute("form") MixedCaseOutputsForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = mixedCaseOutputsFormService.validate(form, bindingResult, validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getMixedOutputsModelAndViewWithErrors(bindingResult, form, submissionDetail),
        () -> {
          mixedCaseOutputsPersistenceService.saveMixedCaseOutputs(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getMixedCaseOutputsModelAndView(MixedCaseOutputsForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/mixedCaseOutputs")
        .addObject("form", form)
        .addObject("oilInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE))
        .addObject("gasInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE))
        .addObject("oilRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES))
        .addObject("associatedGasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES))
        .addObject("gasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES))
        .addObject("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES))
        .addObject("oilInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError());
    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView getMixedOutputsModelAndViewWithErrors(BindingResult bindingResult,
                                                           MixedCaseOutputsForm form,
                                                           SubmissionDetail submissionDetail) {
    var modelAndView = getMixedCaseOutputsModelAndView(form, submissionDetail);

    return modelAndView
        .addObject("oilInPlaceError", probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
            bindingResult,
            "oilInPlace",
            form.getOilInPlace()))
        .addObject("oilRecoverableResourcesError", probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
            bindingResult,
            "oilRecoverableResources",
            form.getOilRecoverableResources()))
        .addObject("associatedGasRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "associatedGasRecoverableResources",
                form.getAssociatedGasRecoverableResources()))
        .addObject("oilEquivalentRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilEquivalentRecoverableResources",
                form.getOilEquivalentRecoverableResources()))
        .addObject("gasInPlaceError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "gasInPlace",
                form.getGasInPlace()))
        .addObject("gasRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "gasRecoverableResources",
                form.getGasRecoverableResources()))
        .addObject("associatedCondensateRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "associatedCondensateRecoverableResources",
                form.getAssociatedCondensateRecoverableResources()));
  }
}
