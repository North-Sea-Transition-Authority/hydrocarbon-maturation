package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class MixedCaseVolumetricsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  private MixedCaseVolumetricsPersistenceService mixedCaseVolumetricsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @BeforeEach
  void setUp() {
    mixedCaseVolumetricsPersistenceService = new MixedCaseVolumetricsPersistenceService(probabilityDistributionPersistenceService);
  }

  @Test
  void saveMixedCaseVolumetrics_whenProspect_whenGivenAForm_verifyCreatedEntity() {
    var form = new MixedCaseVolumetricsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    form.setGasExpansionFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);
    form.setFormationVolumeFactor(probabilityDistributionForm);

    mixedCaseVolumetricsPersistenceService.saveMixedCaseVolumetrics(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT,
            form.getOilRecoveryFactor(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
            form.getGasRecoveryFactor(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR,
            form.getGasExpansionFactor(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            form.getCondensateGasRatioLifeOfField(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            form.getAssociatedGasOilRatioLifeOfField(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_PROSPECT,
            form.getFormationVolumeFactor(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT,
            form.getOilHydrocarbonSaturation(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT,
            form.getGasHydrocarbonSaturation(),
            submissionDetail);
  }

  @Test
  void saveMixedCaseVolumetrics_whenDiscovery_whenGivenAForm_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var form = new MixedCaseVolumetricsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    form.setGasExpansionFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);
    form.setFormationVolumeFactor(probabilityDistributionForm);

    mixedCaseVolumetricsPersistenceService.saveMixedCaseVolumetrics(form, discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_DISCOVERY,
            form.getOilRecoveryFactor(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
            form.getGasRecoveryFactor(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR,
            form.getGasExpansionFactor(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            form.getCondensateGasRatioLifeOfField(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            form.getAssociatedGasOilRatioLifeOfField(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_DISCOVERY,
            form.getFormationVolumeFactor(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_DISCOVERY,
            form.getOilHydrocarbonSaturation(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_DISCOVERY,
            form.getGasHydrocarbonSaturation(),
            discoverySubmissionDetail);
  }
}