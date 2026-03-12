package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class MixedCaseVolumetricParametersPersistenceService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public MixedCaseVolumetricParametersPersistenceService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveMixedCaseVolumetrics(MixedCaseVolumetricParametersForm mixedCaseVolumetricParametersForm,
                                       ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var gasExpansionFactor = ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasExpansionFactor,
        mixedCaseVolumetricParametersForm.getGasExpansionFactor(), submissionDetail, reservoirSubmissionDetail);

    var condensateGasRatioLifeOfField = ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(condensateGasRatioLifeOfField,
        mixedCaseVolumetricParametersForm.getCondensateGasRatioLifeOfField(), submissionDetail, reservoirSubmissionDetail);

    var gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasHydrocarbonSaturation,
        mixedCaseVolumetricParametersForm.getGasHydrocarbonSaturation(), submissionDetail, reservoirSubmissionDetail);

    var gasRecoveryFactor = ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasRecoveryFactor,
        mixedCaseVolumetricParametersForm.getGasRecoveryFactor(), submissionDetail, reservoirSubmissionDetail);

    var oilFormationVolumeFactor = ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(oilFormationVolumeFactor,
        mixedCaseVolumetricParametersForm.getOilFormationVolumeFactor(), submissionDetail, reservoirSubmissionDetail);

    var gasFormationVolumeFactor = ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(gasFormationVolumeFactor,
        mixedCaseVolumetricParametersForm.getGasFormationVolumeFactor(), submissionDetail, reservoirSubmissionDetail);

    var oilHydrocarbonSaturation = ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(oilHydrocarbonSaturation,
        mixedCaseVolumetricParametersForm.getOilHydrocarbonSaturation(), submissionDetail, reservoirSubmissionDetail);

    var associatedGasOilRatioLifeOfField = ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(associatedGasOilRatioLifeOfField,
        mixedCaseVolumetricParametersForm.getAssociatedGasOilRatioLifeOfField(), submissionDetail, reservoirSubmissionDetail);

    var oilRecoveryFactor = ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT;

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(oilRecoveryFactor,
        mixedCaseVolumetricParametersForm.getOilRecoveryFactor(), submissionDetail, reservoirSubmissionDetail);
  }
}
