package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
@RequestMapping("/submission-details/{submissionDetailId}/gas-case-outputs")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class GasCaseOutputsController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final GasCaseOutputsFormService gasCaseOutputsFormService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  public static final String PAGE_NAME = "Gas case outputs (resources)";

  @Autowired
  public GasCaseOutputsController(SubmissionDetailService submissionDetailService,
                                  ControllerHelperService controllerHelperService,
                                  ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                  GasCaseOutputsFormService gasCaseOutputsFormService,
                                  GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.gasCaseOutputsFormService = gasCaseOutputsFormService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
  }

  @GetMapping
  public ModelAndView getGasCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") GasCaseOutputsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getGasCaseOutputsModelAndView(
        gasCaseOutputsFormService.getGasCaseOutputsForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveGasCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @Valid @ModelAttribute("form") GasCaseOutputsForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = gasCaseOutputsFormService.validate(form, bindingResult, validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGasOutputsModelAndViewWithErrors(bindingResult, form, submissionDetail),
        () -> {
          gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getGasCaseOutputsModelAndView(GasCaseOutputsForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasCaseOutputs")
        .addObject("form", form)
        .addObject("gasInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE))
        .addObject("gasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES))
        .addObject("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES))
        .addObject("gasInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError());
    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView getGasOutputsModelAndViewWithErrors(BindingResult bindingResult,
                                                           GasCaseOutputsForm form,
                                                           SubmissionDetail submissionDetail) {
    var modelAndView = getGasCaseOutputsModelAndView(form, submissionDetail);

    return modelAndView
        .addObject("form", form)
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
                form.getAssociatedCondensateRecoverableResources()))
        .addObject("oilEquivalentRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilEquivalentRecoverableResources",
                form.getOilEquivalentRecoverableResources()));
  }
}
