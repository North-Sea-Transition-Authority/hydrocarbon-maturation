package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AllowedSubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
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
@RequestMapping("/submission-details/{submissionDetailId}/oil-case-volumetrics")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.DISCOVERY_AND_PROSPECT)
@IsFromResponsibleTeam
public class OilCaseVolumetricsController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final OilCaseVolumetricsFormService oilCaseVolumetricsFormService;
  private final OilCaseVolumetricsPersistenceService oilCaseVolumetricsPersistenceService;

  public static final String PAGE_NAME = "Oil case volumetrics parameters";

  @Autowired
  public OilCaseVolumetricsController(SubmissionDetailService submissionDetailService,
                                      ControllerHelperService controllerHelperService,
                                      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                      OilCaseVolumetricsFormService oilCaseVolumetricsFormService,
                                      OilCaseVolumetricsPersistenceService oilCaseVolumetricsPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.oilCaseVolumetricsFormService = oilCaseVolumetricsFormService;
    this.oilCaseVolumetricsPersistenceService = oilCaseVolumetricsPersistenceService;
  }

  @GetMapping
  public ModelAndView getOilCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") OilCaseVolumetricsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getOilCaseVolumetricsModelAndView(
        oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveOilCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @Valid @ModelAttribute("form") OilCaseVolumetricsForm form,
                                             BindingResult bindingResult,
                                             ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = oilCaseVolumetricsFormService.validate(form, bindingResult, validationType, submissionDetail);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getOilVolumetricsModelAndViewWithErrors(bindingResult, form, submissionDetail),
        () -> {
          oilCaseVolumetricsPersistenceService.saveOilCaseVolumetrics(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getOilCaseVolumetricsModelAndView(OilCaseVolumetricsForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/oilCaseVolumetrics")
        .addObject("form", form)
        .addObject("oilHydrocarbonSaturationTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION))
        .addObject("associatedGasOilRatioLifeOfFieldTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD))
        .addObject("formationVolumeFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY))
        .addObject("oilRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR))
        .addObject("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError())
        .addObject("formationVolumeFactorError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError());
    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView getOilVolumetricsModelAndViewWithErrors(BindingResult bindingResult,
                                                               OilCaseVolumetricsForm form,
                                                               SubmissionDetail submissionDetail) {
    var modelAndView = getOilCaseVolumetricsModelAndView(form, submissionDetail);

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
                "formationVolumeFactor",
                form.getOilFormationVolumeFactor()))
        .addObject("oilRecoveryFactorError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilRecoveryFactor",
                form.getOilRecoveryFactor()));
  }
}
