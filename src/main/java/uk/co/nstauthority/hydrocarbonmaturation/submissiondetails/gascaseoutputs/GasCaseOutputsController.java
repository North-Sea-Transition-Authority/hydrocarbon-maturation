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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceTypeUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/gas-case-outputs")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_PROSPECT)
@IsFromResponsibleTeam
public class GasCaseOutputsController {

  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final GasCaseOutputsFormService gasCaseOutputsFormService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;
  private final GasCaseOutputsFormValidator gasCaseOutputsFormValidator;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PROSPECT_PAGE_NAME = "Gas case outputs (resources)";
  public static final String DISCOVERY_PAGE_NAME = "Gas case volumetrics";

  @Autowired
  public GasCaseOutputsController(SubmissionDetailService submissionDetailService,
                                  ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                  GasCaseOutputsFormService gasCaseOutputsFormService,
                                  GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService,
                                  GasCaseOutputsFormValidator gasCaseOutputsFormValidator,
                                  ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.gasCaseOutputsFormService = gasCaseOutputsFormService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
    this.gasCaseOutputsFormValidator = gasCaseOutputsFormValidator;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getGasCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getGasCaseOutputsModelAndView(
        gasCaseOutputsFormService.getGasCaseOutputsForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveGasCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                         @Valid @ModelAttribute("form") GasCaseOutputsForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType,
                                         ServiceUserDetail user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getGasOutputsModelAndViewWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, reservoirSubmissionDetail);
      if (submissionDetail.isProspectSubmission()) {
        return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
      } else {
        return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, user));
      }
    }
  }

  private ModelAndView getGasCaseOutputsModelAndView(GasCaseOutputsForm form,
                                                     ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasCaseOutputs")
        .addObject("form", form)
        .addObject("gasInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError());

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      modelAndView
          .addObject("pageTitle", PROSPECT_PAGE_NAME)
          .addObject("gasInPlaceTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT))
          .addObject("gasRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT))
          .addObject("associatedCondensateRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
          .addObject("oilEquivalentRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT));

      TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PROSPECT_PAGE_NAME, submissionDetail);

    } else if (resourceType == ResourceType.DISCOVERY) {
      var contingentUncertaintyCategoryPromptOverrides =
          ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();
      modelAndView
          .addObject("pageTitle", DISCOVERY_PAGE_NAME)
          .addObject("gasInPlaceTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY))
          .addObject("gasRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                  ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                  contingentUncertaintyCategoryPromptOverrides))
          .addObject("associatedCondensateRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                  ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
                  contingentUncertaintyCategoryPromptOverrides))
          .addObject("oilEquivalentRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                  ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
                  contingentUncertaintyCategoryPromptOverrides));

      TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(
          modelAndView, DISCOVERY_PAGE_NAME, reservoirSubmissionDetail
      );
    }
    return modelAndView;
  }

  private ModelAndView getGasOutputsModelAndViewWithErrors(BindingResult bindingResult,
                                                           GasCaseOutputsForm form,
                                                           ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = getGasCaseOutputsModelAndView(form, reservoirSubmissionDetail);

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
