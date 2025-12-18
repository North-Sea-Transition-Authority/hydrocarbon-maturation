package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricParametersPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  private GasCaseVolumetricParametersPersistenceService gasCaseVolumetricParametersPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @BeforeEach
  void setUp() {
    gasCaseVolumetricParametersPersistenceService = new GasCaseVolumetricParametersPersistenceService(probabilityDistributionPersistenceService);
  }

  @Test
  void saveGasCaseVolumetrics_whenProspect_whenGivenAForm_verifyCreatedEntity() {
    var form = new GasCaseVolumetricParametersForm();
    form.setGasFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setCondensateGasRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    gasCaseVolumetricParametersPersistenceService.saveGasCaseVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
            form.getGasFormationVolumeFactor(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
            form.getGasRecoveryFactor(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            form.getGasExpansionFactor(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT,
            form.getGasHydrocarbonSaturation(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            form.getCondensateGasRatioLifeOfField(),
            submissionDetail,
            reservoirSubmissionDetail);
  }

  @Test
  void saveGasCaseVolumetrics_whenDiscovery_whenGivenAForm_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var form = new GasCaseVolumetricParametersForm();
    form.setGasFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setCondensateGasRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    gasCaseVolumetricParametersPersistenceService.saveGasCaseVolumetrics(form, discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
            form.getGasFormationVolumeFactor(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
            form.getGasRecoveryFactor(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            form.getGasExpansionFactor(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
            form.getGasHydrocarbonSaturation(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            form.getCondensateGasRatioLifeOfField(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);
  }
}