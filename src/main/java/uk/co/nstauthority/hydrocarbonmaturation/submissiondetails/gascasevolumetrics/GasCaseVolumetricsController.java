package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

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
    "reservoir-submission-details/{reservoirSubmissionDetailId}/gas-case-volumetrics")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_PROSPECT)
@IsFromResponsibleTeam
public class GasCaseVolumetricsController {

  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final GasCaseVolumetricsFormService gasCaseVolumetricsFormService;
  private final GasCaseVolumetricsPersistenceService gasCaseVolumetricsPersistenceService;
  private final GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Gas case volumetrics parameters";

  @Autowired
  public GasCaseVolumetricsController(SubmissionDetailService submissionDetailService,
                                      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                      GasCaseVolumetricsFormService gasCaseVolumetricsFormService,
                                      GasCaseVolumetricsPersistenceService gasCaseVolumetricsPersistenceService,
                                      GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator,
                                      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.gasCaseVolumetricsFormService = gasCaseVolumetricsFormService;
    this.gasCaseVolumetricsPersistenceService = gasCaseVolumetricsPersistenceService;
    this.gasCaseVolumetricsFormValidator = gasCaseVolumetricsFormValidator;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getGasCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getGasCaseVolumetricsModelAndView(
        gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveGasCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                             @ModelAttribute("form") GasCaseVolumetricsForm form,
                                             BindingResult bindingResult,
                                             ValidationType validationType,
                                             ServiceUserDetail user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!gasCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getGasVolumetricsModelAndViewWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      gasCaseVolumetricsPersistenceService.saveGasCaseVolumetrics(form, reservoirSubmissionDetail);
      if (submissionDetail.isProspectSubmission()) {
        return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
      } else {
        return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, user));
      }
    }
  }

  private ModelAndView getGasCaseVolumetricsModelAndView(GasCaseVolumetricsForm form,
                                                         ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasCaseVolumetrics")
        .addObject("form", form)
        .addObject("condensateGasRatioLifeOfFieldTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD))
        .addObject("gasRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR))
        .addObject("gasFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError())
        .addObject("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError());

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      modelAndView
          .addObject("gasFormationVolumeFactorTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT))
          .addObject("gasHydrocarbonSaturationTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT));

      TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    } else if (resourceType == ResourceType.DISCOVERY) {
      modelAndView
          .addObject("gasFormationVolumeFactorTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY))
          .addObject("gasHydrocarbonSaturationTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY));

      TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);
    }
    return modelAndView;
  }

  private ModelAndView getGasVolumetricsModelAndViewWithErrors(BindingResult bindingResult,
                                                               GasCaseVolumetricsForm form,
                                                               ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = getGasCaseVolumetricsModelAndView(form, reservoirSubmissionDetail);

    return modelAndView
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
