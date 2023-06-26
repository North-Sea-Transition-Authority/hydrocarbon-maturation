package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasCaseVolumetricsFormService implements SubmissionFormService {

  private final GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  public GasCaseVolumetricsFormService(
      GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator,
      ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.gasCaseVolumetricsFormValidator = gasCaseVolumetricsFormValidator;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    gasCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  GasCaseVolumetricsForm getGasCaseVolumetricsForm(SubmissionDetail submissionDetail) {
    return submissionDetailToForm(submissionDetail);
  }

  private GasCaseVolumetricsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new GasCaseVolumetricsForm();

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setGasHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT
      ));

      form.setGasFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setGasHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY
      ));

      form.setGasFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY
      ));
    }

    form.setGasExpansionFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR
    ));

    form.setGasRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
    ));

    form.setCondensateGasRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD
    ));

    return form;
  }
}
