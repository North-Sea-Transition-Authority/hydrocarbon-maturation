package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class OilCaseVolumetricParametersPersistenceService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public OilCaseVolumetricParametersPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveOilCaseVolumetrics(OilCaseVolumetricParametersForm oilCaseVolumetricParametersForm,
                                     ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var oilHydrocarbonSaturation =
        ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilHydrocarbonSaturation = ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(oilHydrocarbonSaturation,
        oilCaseVolumetricParametersForm.getOilHydrocarbonSaturation(), submissionDetail, reservoirSubmissionDetail);

    var formationVolumeFactor = ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      formationVolumeFactor = ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(formationVolumeFactor,
        oilCaseVolumetricParametersForm.getOilFormationVolumeFactor(), submissionDetail, reservoirSubmissionDetail);

    var oilRecoveryFactor = ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      oilRecoveryFactor = ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(oilRecoveryFactor,
        oilCaseVolumetricParametersForm.getOilRecoveryFactor(), submissionDetail, reservoirSubmissionDetail);

    var associatedGasOilRatioLifeOfField =
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(associatedGasOilRatioLifeOfField,
        oilCaseVolumetricParametersForm.getAssociatedGasOilRatioLifeOfField(), submissionDetail, reservoirSubmissionDetail);
  }
}
