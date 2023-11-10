package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceTypeUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/discovery-gas-case-totals")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_TYPES)
@IsFromResponsibleTeam
public class DiscoveryGasCaseVolumetricTotalsController {

  public static final String PAGE_NAME = "Total gas case volumetrics";

  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;
  private final DiscoveryGasCaseVolumetricTotalsFormValidator discoveryGasCaseVolumetricTotalsFormValidator;

  @Autowired
  public DiscoveryGasCaseVolumetricTotalsController(
      SubmissionDetailService submissionDetailService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService,
      DiscoveryGasCaseVolumetricTotalsFormValidator discoveryGasCaseVolumetricTotalsFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.discoveryGasCaseVolumetricTotalsService = discoveryGasCaseVolumetricTotalsService;
    this.discoveryGasCaseVolumetricTotalsFormValidator = discoveryGasCaseVolumetricTotalsFormValidator;
  }

  @GetMapping
  public ModelAndView getDiscoveryGasCaseVolumetricTotals(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getDiscoveryGasCaseVolumetricTotalsModelAndView(
        discoveryGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveDiscoveryGasCaseVolumetricTotals(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                           @ModelAttribute("form") GasCaseOutputsForm form,
                                                           BindingResult bindingResult,
                                                           ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getDiscoveryGasCaseVolumetricTotalsModelAndViewWithErrors(bindingResult, form, submissionDetail);
    } else {
      discoveryGasCaseVolumetricTotalsService.saveDiscoveryGasCaseVolumetricTotals(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
  }

  private ModelAndView getDiscoveryGasCaseVolumetricTotalsModelAndView(GasCaseOutputsForm form,
                                                                       SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasCaseOutputs")
        .addObject("form", form)
        .addObject("resourceType",  submissionDetail.getSubmission().getResource())
        .addObject("gasInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError());
    var contingentUncertaintyCategoryPromptOverrides =
        ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();
    modelAndView
        .addObject("pageTitle", PAGE_NAME)
        .addObject("gasInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY))
        .addObject("gasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides))
        .addObject("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }

  private ModelAndView getDiscoveryGasCaseVolumetricTotalsModelAndViewWithErrors(BindingResult bindingResult,
                                                                                 GasCaseOutputsForm form,
                                                                                 SubmissionDetail submissionDetail) {
    var modelAndView = getDiscoveryGasCaseVolumetricTotalsModelAndView(form, submissionDetail);

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
