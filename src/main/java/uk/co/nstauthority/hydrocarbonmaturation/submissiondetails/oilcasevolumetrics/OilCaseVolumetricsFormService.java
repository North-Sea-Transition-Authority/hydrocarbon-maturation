package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseVolumetricsFormService implements SubmissionFormService {

  private final OilCaseVolumetricsFormValidator oilCaseVolumetricsFormValidator;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  public OilCaseVolumetricsFormService(
      OilCaseVolumetricsFormValidator oilCaseVolumetricsFormValidator,
      ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.oilCaseVolumetricsFormValidator = oilCaseVolumetricsFormValidator;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    oilCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  OilCaseVolumetricsForm getOilCaseVolumetricsForm(SubmissionDetail submissionDetail) {
    return submissionDetailToForm(submissionDetail);
  }

  private OilCaseVolumetricsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new OilCaseVolumetricsForm();

    form.setOilHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION
    ));

    form.setOilFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY
    ));

    form.setOilRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR
    ));

    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD
    ));

    return form;
  }
}
