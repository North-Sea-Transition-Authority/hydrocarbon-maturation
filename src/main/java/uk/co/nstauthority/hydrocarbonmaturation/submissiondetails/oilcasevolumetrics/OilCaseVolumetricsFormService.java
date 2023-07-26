package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
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

  OilCaseVolumetricsForm getOilCaseVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private OilCaseVolumetricsForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new OilCaseVolumetricsForm();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setOilHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT
      ));

      form.setOilFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT
      ));

      form.setOilRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setOilHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY
      ));

      form.setOilFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY
      ));

      form.setOilRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY
      ));
    }

    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD
    ));

    return form;
  }
}
