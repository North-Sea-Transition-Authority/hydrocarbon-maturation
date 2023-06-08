package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    if (gasCaseVolumetricsForm.getGasFormationVolumeFactor().getProbabilityDistributionType() != null) {
      var gasFormationVolumeFactorProspectDiscovery =
          ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          gasFormationVolumeFactorProspectDiscovery,
          gasCaseVolumetricsForm.getGasFormationVolumeFactor(),
          submissionDetail
      );
    }

    if (gasCaseVolumetricsForm.getGasHydrocarbonSaturation().getProbabilityDistributionType() != null) {
      var gasHydrocarbonSaturation =
          ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          gasHydrocarbonSaturation,
          gasCaseVolumetricsForm.getGasHydrocarbonSaturation(),
          submissionDetail
      );
    }

    if (gasCaseVolumetricsForm.getGasExpansionFactor().getProbabilityDistributionType() != null) {
      var gasExpansionFactor = ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          gasExpansionFactor,
          gasCaseVolumetricsForm.getGasExpansionFactor(),
          submissionDetail
      );
    }

    if (gasCaseVolumetricsForm.getGasRecoveryFactor().getProbabilityDistributionType() != null) {
      var gasRecoveryFactor = ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          gasRecoveryFactor,
          gasCaseVolumetricsForm.getGasRecoveryFactor(),
          submissionDetail
      );
    }

    if (gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField().getProbabilityDistributionType() != null) {
      var condensateGasRatioLifeOfField =
          ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          condensateGasRatioLifeOfField,
          gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField(),
          submissionDetail
      );
    }
  }
}
