package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class OilCaseVolumetricsPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public OilCaseVolumetricsPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveOilCaseVolumetrics(OilCaseVolumetricsForm oilCaseVolumetricsForm, SubmissionDetail submissionDetail) {
    if (oilCaseVolumetricsForm.getOilHydrocarbonSaturation().getProbabilityDistributionType() != null) {
      var oilHydrocarbonSaturation =
          ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilHydrocarbonSaturation,
          oilCaseVolumetricsForm.getOilHydrocarbonSaturation(),
          submissionDetail
      );
    }

    if (oilCaseVolumetricsForm.getFormationVolumeFactor().getProbabilityDistributionType() != null) {
      var formationVolumeFactor = ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          formationVolumeFactor,
          oilCaseVolumetricsForm.getFormationVolumeFactor(),
          submissionDetail
      );
    }

    if (oilCaseVolumetricsForm.getOilRecoveryFactor().getProbabilityDistributionType() != null) {
      var oilRecoveryFactor = ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          oilRecoveryFactor,
          oilCaseVolumetricsForm.getOilRecoveryFactor(),
          submissionDetail
      );
    }

    if (oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType() != null) {
      var associatedGasOilRatioLifeOfField =
          ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          associatedGasOilRatioLifeOfField,
          oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField(),
          submissionDetail
      );
    }
  }
}
