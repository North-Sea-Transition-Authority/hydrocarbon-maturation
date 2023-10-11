package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class OilCaseVolumetricParametersFormService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  public OilCaseVolumetricParametersFormService(ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  OilCaseVolumetricParametersForm getOilCaseVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private OilCaseVolumetricParametersForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new OilCaseVolumetricParametersForm();
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
