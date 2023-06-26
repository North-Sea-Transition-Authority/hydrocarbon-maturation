package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class MixedCaseVolumetricsPersistenceService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public MixedCaseVolumetricsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveMixedCaseVolumetrics(MixedCaseVolumetricsForm mixedCaseVolumetricsForm,
                                       SubmissionDetail submissionDetail) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var condensateGasRatioLifeOfField = ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(condensateGasRatioLifeOfField,
        mixedCaseVolumetricsForm.getCondensateGasRatioLifeOfField(), submissionDetail);

    var gasExpansionFactor = ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasExpansionFactor,
        mixedCaseVolumetricsForm.getGasExpansionFactor(), submissionDetail);

    var gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasHydrocarbonSaturation,
        mixedCaseVolumetricsForm.getGasHydrocarbonSaturation(), submissionDetail);

    var gasRecoveryFactor = ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasRecoveryFactor,
        mixedCaseVolumetricsForm.getGasRecoveryFactor(), submissionDetail);

    var formationVolumeFactor = ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      formationVolumeFactor = ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(formationVolumeFactor,
        mixedCaseVolumetricsForm.getFormationVolumeFactor(), submissionDetail);

    var oilHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(oilHydrocarbonSaturation,
        mixedCaseVolumetricsForm.getOilHydrocarbonSaturation(), submissionDetail);

    var associatedGasOilRatioLifeOfField = ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(associatedGasOilRatioLifeOfField,
        mixedCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField(), submissionDetail);

    var oilRecoveryFactor = ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilRecoveryFactor = ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(oilRecoveryFactor,
        mixedCaseVolumetricsForm.getOilRecoveryFactor(), submissionDetail);
  }
}
