package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class GasCaseVolumetricsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public GasCaseVolumetricsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveGasCaseVolumetrics(GasCaseVolumetricsForm gasCaseVolumetricsForm, SubmissionDetail submissionDetail) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var gasFormationVolumeFactor = ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      gasFormationVolumeFactor = ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasFormationVolumeFactor,
        gasCaseVolumetricsForm.getGasFormationVolumeFactor(), submissionDetail);

    var gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasHydrocarbonSaturation,
        gasCaseVolumetricsForm.getGasHydrocarbonSaturation(), submissionDetail);

    var gasExpansionFactor = ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasExpansionFactor,
        gasCaseVolumetricsForm.getGasExpansionFactor(), submissionDetail);

    var gasRecoveryFactor = ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasRecoveryFactor,
        gasCaseVolumetricsForm.getGasRecoveryFactor(), submissionDetail);

    var condensateGasRatioLifeOfField = ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(condensateGasRatioLifeOfField,
        gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField(), submissionDetail);
  }
}
