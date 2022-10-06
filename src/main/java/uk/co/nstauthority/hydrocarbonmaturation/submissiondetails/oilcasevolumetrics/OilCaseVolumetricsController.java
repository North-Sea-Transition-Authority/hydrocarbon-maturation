package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/oil-case-volumetrics")
public class OilCaseVolumetricsController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final OilCaseVolumetricsFormService oilCaseVolumetricsFormService;
  private final OilCaseVolumetricsPersistenceService oilCaseVolumetricsPersistenceService;

  public static final String PAGE_NAME = "Oil case volumetrics";

  @Autowired
  public OilCaseVolumetricsController(SubmissionDetailService submissionDetailService,
                                      ControllerHelperService controllerHelperService,
                                      SubmissionTaskListRedirectionService submissionTaskListRedirectionService,
                                      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                      OilCaseVolumetricsFormService oilCaseVolumetricsFormService,
                                      OilCaseVolumetricsPersistenceService oilCaseVolumetricsPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.oilCaseVolumetricsFormService = oilCaseVolumetricsFormService;
    this.oilCaseVolumetricsPersistenceService = oilCaseVolumetricsPersistenceService;
  }

  @GetMapping
  public ModelAndView getOilCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") OilCaseVolumetricsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getOilCaseVolumetricsModelAndView(
        oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveOilCaseVolumetrics(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @Valid @ModelAttribute("form") OilCaseVolumetricsForm form,
                                             BindingResult bindingResult,
                                             ValidationType validationType) {
    bindingResult = oilCaseVolumetricsFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getOilVolumetricsModelAndViewWithErrors(bindingResult, form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          oilCaseVolumetricsPersistenceService.saveOilCaseVolumetrics(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
        }
    );
  }

  private ModelAndView getOilCaseVolumetricsModelAndView(OilCaseVolumetricsForm form, UUID submissionDetailId) {
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
                ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY))
        .addObject("oilRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR))
        .addObject("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError())
        .addObject("formationVolumeFactorError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError());
    ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
    return modelAndView;
  }

  private ModelAndView getOilVolumetricsModelAndViewWithErrors(BindingResult bindingResult,
                                                               OilCaseVolumetricsForm form,
                                                               UUID submissionDetailId) {
    var modelAndView = getOilCaseVolumetricsModelAndView(form, submissionDetailId);

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
                form.getFormationVolumeFactor()))
        .addObject("oilRecoveryFactorError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilRecoveryFactor",
                form.getOilRecoveryFactor()));
  }
}
