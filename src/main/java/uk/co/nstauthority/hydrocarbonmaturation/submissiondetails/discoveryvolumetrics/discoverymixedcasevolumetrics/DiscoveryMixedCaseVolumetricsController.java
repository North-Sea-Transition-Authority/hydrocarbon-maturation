package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceTypeUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/discovery-mixed-case-volumetrics")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_TYPES)
@IsFromResponsibleTeam
public class DiscoveryMixedCaseVolumetricsController {

  public static final String PAGE_NAME = "Oil and gas case volumetrics";

  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final DiscoveryMixedCaseVolumetricsFormService discoveryMixedCaseVolumetricsFormService;
  private final DiscoveryMixedCaseVolumetricsFormValidator discoveryMixedCaseVolumetricsFormValidator;
  private final DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public DiscoveryMixedCaseVolumetricsController(
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      DiscoveryMixedCaseVolumetricsFormService discoveryMixedCaseVolumetricsFormService,
      DiscoveryMixedCaseVolumetricsFormValidator discoveryMixedCaseVolumetricsFormValidator,
      DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.discoveryMixedCaseVolumetricsFormService = discoveryMixedCaseVolumetricsFormService;
    this.discoveryMixedCaseVolumetricsFormValidator = discoveryMixedCaseVolumetricsFormValidator;
    this.discoveryMixedCaseVolumetricsPersistenceService = discoveryMixedCaseVolumetricsPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getMixedCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getMixedCaseOutputsModelAndView(
        discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveMixedCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                           @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                           @Valid @ModelAttribute("form") MixedCaseOutputsForm form,
                                           BindingResult bindingResult,
                                           ValidationType validationType,
                                           ServiceUserDetail user) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!discoveryMixedCaseVolumetricsFormValidator.isValid(
        form, bindingResult, reservoirSubmissionDetail.getSubmissionDetail(), validationType)) {
      return getMixedOutputsModelAndViewWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      discoveryMixedCaseVolumetricsPersistenceService.saveMixedCaseOutputs(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, user));
    }
  }

  private ModelAndView getMixedCaseOutputsModelAndView(MixedCaseOutputsForm form,
                                                       ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/mixedCaseOutputs")
        .addObject("form", form)
        .addObject("resourceType",
            reservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType().toString())
        .addObject("pageTitle", PAGE_NAME)
        .addObject("oilInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError());

    var contingentUncertaintyCategoryPromptOverrides =
          ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();
    modelAndView
        .addObject("oilInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY))
        .addObject("gasInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY))
        .addObject("oilRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides))
        .addObject("associatedGasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides))
        .addObject("gasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides))
        .addObject("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides));

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);
    return modelAndView;
  }

  private ModelAndView getMixedOutputsModelAndViewWithErrors(BindingResult bindingResult,
                                                             MixedCaseOutputsForm form,
                                                             ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = getMixedCaseOutputsModelAndView(form, reservoirSubmissionDetail);

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
