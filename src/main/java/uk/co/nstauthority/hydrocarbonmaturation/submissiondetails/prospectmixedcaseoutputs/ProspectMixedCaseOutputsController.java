package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/mixed-case-outputs")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.PROSPECT_TYPES)
@IsFromResponsibleTeam
public class ProspectMixedCaseOutputsController {

  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final ProspectMixedCaseOutputsFormService prospectMixedCaseOutputsFormService;
  private final ProspectMixedCaseOutputsFormValidator prospectMixedCaseOutputsFormValidator;
  private final ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Mixed case outputs (resources)";

  @Autowired
  public ProspectMixedCaseOutputsController(
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      ProspectMixedCaseOutputsFormService prospectMixedCaseOutputsFormService,
      ProspectMixedCaseOutputsFormValidator prospectMixedCaseOutputsFormValidator,
      ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.prospectMixedCaseOutputsFormService = prospectMixedCaseOutputsFormService;
    this.prospectMixedCaseOutputsFormValidator = prospectMixedCaseOutputsFormValidator;
    this.prospectMixedCaseOutputsPersistenceService = prospectMixedCaseOutputsPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getMixedCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return getMixedCaseOutputsModelAndView(
        prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveMixedCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                           @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                           @Valid @ModelAttribute("form") MixedCaseOutputsForm form,
                                           BindingResult bindingResult,
                                           ValidationType validationType) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (!prospectMixedCaseOutputsFormValidator.isValid(
        form, bindingResult, submissionDetail, validationType)) {
      return getMixedOutputsModelAndViewWithErrors(bindingResult, form, submissionDetail);
    } else {
      prospectMixedCaseOutputsPersistenceService.saveMixedCaseOutputs(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
  }

  private ModelAndView getMixedCaseOutputsModelAndView(MixedCaseOutputsForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/mixedCaseOutputs")
        .addObject("form", form)
        .addObject("resourceType", submissionDetail.getSubmission().getResource().getType().toString())
        .addObject("pageTitle", PAGE_NAME)
        .addObject("oilInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("riskedOilEquivalentRecoverableMeanError", ProbabilityDistributionTypeError.emptyError());

    modelAndView
        .addObject("oilInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .addObject("gasInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .addObject("oilRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .addObject("associatedGasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .addObject("gasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .addObject("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .addObject("riskedOilEquivalentRecoverableMeanTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT));

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
                form.getAssociatedCondensateRecoverableResources()))
        .addObject("riskedOilEquivalentRecoverableMeanError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "riskedOilEquivalentRecoverableMean",
                form.getRiskedOilEquivalentRecoverableMean()));
  }
}
