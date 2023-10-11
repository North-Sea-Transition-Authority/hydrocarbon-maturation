package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class GasCaseVolumetricParametersFormService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  public GasCaseVolumetricParametersFormService(
      ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  GasCaseVolumetricParametersForm getGasCaseVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private GasCaseVolumetricParametersForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new GasCaseVolumetricParametersForm();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setGasHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT
      ));

      form.setGasFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setGasHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY
      ));

      form.setGasFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY
      ));
    }

    form.setGasRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
    ));

    form.setCondensateGasRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD
    ));

    return form;
  }
}
