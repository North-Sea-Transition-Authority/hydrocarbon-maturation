package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    if (mixedCaseVolumetricsForm.getCondensateGasRatioLifeOfField().getProbabilityDistributionType() != null) {
      var condensateGasRatioLifeOfField = ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          condensateGasRatioLifeOfField,
          mixedCaseVolumetricsForm.getCondensateGasRatioLifeOfField(),
          submissionDetail
      );
    }

    if (mixedCaseVolumetricsForm.getGasExpansionFactor().getProbabilityDistributionType() != null) {
      var gasExpansionFactor = ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          gasExpansionFactor,
          mixedCaseVolumetricsForm.getGasExpansionFactor(),
          submissionDetail
      );
    }

    if (mixedCaseVolumetricsForm.getGasHydrocarbonSaturation().getProbabilityDistributionType() != null) {
      var gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          gasHydrocarbonSaturation,
          mixedCaseVolumetricsForm.getGasHydrocarbonSaturation(),
          submissionDetail
      );
    }

    if (mixedCaseVolumetricsForm.getGasRecoveryFactor().getProbabilityDistributionType() != null) {
      var gasRecoveryFactor = ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          gasRecoveryFactor,
          mixedCaseVolumetricsForm.getGasRecoveryFactor(),
          submissionDetail
      );
    }

    if (mixedCaseVolumetricsForm.getFormationVolumeFactor().getProbabilityDistributionType() != null) {
      var formationVolumeFactor = ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          formationVolumeFactor,
          mixedCaseVolumetricsForm.getFormationVolumeFactor(),
          submissionDetail
      );
    }

    if (mixedCaseVolumetricsForm.getOilHydrocarbonSaturation().getProbabilityDistributionType() != null) {
      var oilHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilHydrocarbonSaturation,
          mixedCaseVolumetricsForm.getOilHydrocarbonSaturation(),
          submissionDetail
      );
    }

    if (mixedCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType() != null) {
      var associatedGasOilRatioLifeOfField = ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          associatedGasOilRatioLifeOfField,
          mixedCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField(),
          submissionDetail
      );
    }

    if (mixedCaseVolumetricsForm.getOilRecoveryFactor().getProbabilityDistributionType() != null) {
      var oilRecoveryFactor = ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilRecoveryFactor,
          mixedCaseVolumetricsForm.getOilRecoveryFactor(),
          submissionDetail
      );
    }
  }
}
