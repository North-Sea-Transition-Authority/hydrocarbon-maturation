package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

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
class GasCaseVolumetricsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  private GasCaseVolumetricsPersistenceService gasCaseVolumetricsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @BeforeEach
  void setUp() {
    gasCaseVolumetricsPersistenceService = new GasCaseVolumetricsPersistenceService(probabilityDistributionPersistenceService);
  }

  @Test
  void saveGasCaseVolumetrics_whenGivenAForm_verifyCreatedEntity() {
    var form = new GasCaseVolumetricsForm();
    form.setGasHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasExpansionFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setCondensateGasRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    gasCaseVolumetricsPersistenceService.saveGasCaseVolumetrics(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            form.getGasExpansionFactor(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
            form.getGasRecoveryFactor(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION,
            form.getGasHydrocarbonSaturation(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            form.getCondensateGasRatioLifeOfField(),
            submissionDetail);
  }
}