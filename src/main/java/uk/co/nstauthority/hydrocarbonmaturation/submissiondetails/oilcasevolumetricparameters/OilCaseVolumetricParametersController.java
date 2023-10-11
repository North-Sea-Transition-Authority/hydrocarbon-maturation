package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

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
    "reservoir-submission-details/{reservoirSubmissionDetailId}/oil-case-volumetrics")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_PROSPECT)
@IsFromResponsibleTeam
public class OilCaseVolumetricParametersController {

  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final OilCaseVolumetricParametersFormService oilCaseVolumetricParametersFormService;
  private final OilCaseVolumetricParametersPersistenceService oilCaseVolumetricParametersPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final OilCaseVolumetricParametersFormValidator oilCaseVolumetricParametersFormValidator;

  public static final String PAGE_NAME = "Oil case volumetric parameters";

  @Autowired
  public OilCaseVolumetricParametersController(
      SubmissionDetailService submissionDetailService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      OilCaseVolumetricParametersFormService oilCaseVolumetricParametersFormService,
      OilCaseVolumetricParametersPersistenceService oilCaseVolumetricParametersPersistenceService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      OilCaseVolumetricParametersFormValidator oilCaseVolumetricParametersFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.oilCaseVolumetricParametersFormService = oilCaseVolumetricParametersFormService;
    this.oilCaseVolumetricParametersPersistenceService = oilCaseVolumetricParametersPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.oilCaseVolumetricParametersFormValidator = oilCaseVolumetricParametersFormValidator;
  }

  @GetMapping
  public ModelAndView getOilCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getOilCaseVolumetricsModelAndView(
        oilCaseVolumetricParametersFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveOilCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                             @ModelAttribute("form") OilCaseVolumetricParametersForm form,
                                             BindingResult bindingResult,
                                             ValidationType validationType,
                                             ServiceUserDetail user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getOilVolumetricsModelAndViewWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      oilCaseVolumetricParametersPersistenceService.saveOilCaseVolumetrics(form, reservoirSubmissionDetail);
      if (submissionDetail.isProspectSubmission()) {
        return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
      } else {
        return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, user));
      }
    }
  }

  private ModelAndView getOilCaseVolumetricsModelAndView(OilCaseVolumetricParametersForm form,
                                                         ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/oilCaseVolumetrics")
        .addObject("form", form)
        .addObject("associatedGasOilRatioLifeOfFieldTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD))
        .addObject("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError())
        .addObject("formationVolumeFactorError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError());

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      modelAndView
          .addObject("oilHydrocarbonSaturationTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT))
          .addObject("formationVolumeFactorTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT))
          .addObject("oilRecoveryFactorTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT));

      TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    } else if (resourceType == ResourceType.DISCOVERY) {
      modelAndView
          .addObject("oilHydrocarbonSaturationTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY))
          .addObject("formationVolumeFactorTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY))
          .addObject("oilRecoveryFactorTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY));

      TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);

    }
    return modelAndView;
  }

  private ModelAndView getOilVolumetricsModelAndViewWithErrors(BindingResult bindingResult,
                                                               OilCaseVolumetricParametersForm form,
                                                               ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = getOilCaseVolumetricsModelAndView(form, reservoirSubmissionDetail);

    return modelAndView
        .addObject("oilHydrocarbonSaturationError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilHydrocarbonSaturation",
                form.getOilHydrocarbonSaturation()))
        .addObject("associatedGasOilRatioLifeOfFieldError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "associatedGasOilRatioLifeOfField",
                form.getAssociatedGasOilRatioLifeOfField()))
        .addObject("formationVolumeFactorError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilFormationVolumeFactor",
                form.getOilFormationVolumeFactor()))
        .addObject("oilRecoveryFactorError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilRecoveryFactor",
                form.getOilRecoveryFactor()));
  }
}
