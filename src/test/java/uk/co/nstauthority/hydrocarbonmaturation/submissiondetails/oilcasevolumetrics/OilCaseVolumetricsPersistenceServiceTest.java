package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

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
class OilCaseVolumetricsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  private OilCaseVolumetricsPersistenceService oilCaseVolumetricsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @BeforeEach
  void setUp() {
    oilCaseVolumetricsPersistenceService = new OilCaseVolumetricsPersistenceService(probabilityDistributionPersistenceService);
  }

  @Test
  void saveOilCaseVolumetrics_whenProspect_whenGivenAForm_verifyCreatedEntity() {
    var form = new OilCaseVolumetricsForm();
    form.setOilHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setOilFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setOilRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setAssociatedGasOilRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    oilCaseVolumetricsPersistenceService.saveOilCaseVolumetrics(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT,
            form.getOilHydrocarbonSaturation(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT,
            form.getOilRecoveryFactor(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            form.getAssociatedGasOilRatioLifeOfField(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
            form.getOilFormationVolumeFactor(),
            submissionDetail,
            reservoirSubmissionDetail);
  }

  @Test
  void saveOilCaseVolumetrics_whenDiscovery_whenGivenAForm_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var form = new OilCaseVolumetricsForm();
    form.setOilHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setOilFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setOilRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setAssociatedGasOilRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    oilCaseVolumetricsPersistenceService.saveOilCaseVolumetrics(form, discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY,
            form.getOilHydrocarbonSaturation(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY,
            form.getOilRecoveryFactor(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            form.getAssociatedGasOilRatioLifeOfField(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
            form.getOilFormationVolumeFactor(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);
  }
}