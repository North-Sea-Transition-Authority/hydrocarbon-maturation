package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/field-gas-case-volumetric-parameters")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class FieldGasVolumetricParametersController {

  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator;
  private final FieldGasVolumetricParametersService fieldGasVolumetricParametersService;

  public static final String PAGE_NAME = "Gas volumetric parameters";

  public FieldGasVolumetricParametersController(
      SubmissionDetailService submissionDetailService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator,
      FieldGasVolumetricParametersService fieldGasVolumetricParametersService) {
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.gasCaseVolumetricParametersFormValidator = gasCaseVolumetricParametersFormValidator;
    this.fieldGasVolumetricParametersService = fieldGasVolumetricParametersService;
  }

  @GetMapping
  public ModelAndView getGasVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getGasVolumetricsModelAndView(
        fieldGasVolumetricParametersService.getGasVolumetricsForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveGasVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                         @ModelAttribute("form") GasCaseVolumetricParametersForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType,
                                         User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getGasVolumetricsModelAndViewWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      fieldGasVolumetricParametersService.saveGasVolumetrics(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId));
    }
  }

  private ModelAndView getGasVolumetricsModelAndView(GasCaseVolumetricParametersForm form,
                                                     ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasCaseVolumetrics")
        .addObject("form", form)
        .addObject("condensateGasRatioLifeOfFieldTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT))
        .addObject("gasRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD))
        .addObject("gasExpansionFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR_FIELD))
        .addObject("gasFormationVolumeFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD))
        .addObject("gasHydrocarbonSaturationTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD))
        .addObject("gasExpansionFactorError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError())
        .addObject("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError());

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);

    return modelAndView;
  }

  private ModelAndView getGasVolumetricsModelAndViewWithErrors(BindingResult bindingResult,
                                                               GasCaseVolumetricParametersForm form,
                                                               ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = getGasVolumetricsModelAndView(form, reservoirSubmissionDetail);

    return modelAndView
        .addObject("gasExpansionFactorError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "gasExpansionFactor",
                form.getGasExpansionFactor()))
        .addObject("gasFormationVolumeFactorError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "gasFormationVolumeFactor",
                form.getGasFormationVolumeFactor()))
        .addObject("gasHydrocarbonSaturationError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "gasHydrocarbonSaturation",
                form.getGasHydrocarbonSaturation()))
        .addObject("condensateGasRatioLifeOfFieldError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "condensateGasRatioLifeOfField",
                form.getCondensateGasRatioLifeOfField()))
        .addObject("gasRecoveryFactorError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "gasRecoveryFactor",
                form.getGasRecoveryFactor()));
  }
}
