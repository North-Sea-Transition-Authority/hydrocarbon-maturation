package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class MixedCaseVolumetricsFormService implements SubmissionFormService {

  private final MixedCaseVolumetricsFormValidator mixedCaseVolumetricsFormValidator;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  public MixedCaseVolumetricsFormService(MixedCaseVolumetricsFormValidator mixedCaseVolumetricsFormValidator,
                                         ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.mixedCaseVolumetricsFormValidator = mixedCaseVolumetricsFormValidator;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    mixedCaseVolumetricsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  MixedCaseVolumetricsForm getMixedCaseVolumetricsForm(SubmissionDetail submissionDetail) {
    return submissionDetailToForm(submissionDetail);
  }

  private MixedCaseVolumetricsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new MixedCaseVolumetricsForm();

    form.setFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY
    ));

    form.setOilRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR
    ));

    form.setGasRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR
    ));

    form.setCondensateGasRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD
    ));

    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD
    ));

    form.setGasExpansionFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR
    ));

    form.setGasHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION
    ));

    form.setOilHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION
    ));

    return form;
  }
}
