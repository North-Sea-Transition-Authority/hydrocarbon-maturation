package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import static org.mockito.ArgumentMatchers.any;
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
class GasCaseOutputsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private GasCaseOutputsRepository gasCaseOutputsRepository;

  private GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @BeforeEach
  void setUp() {
    gasCaseOutputsPersistenceService = new GasCaseOutputsPersistenceService(probabilityDistributionPersistenceService,
        gasCaseOutputsRepository);
  }

  @Test
  void saveGasCaseOutputs_whenGivenAForm_verifyCreatedEntity() {
    var form = new GasCaseOutputsForm();
    form.setGasInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedCondensateRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(gasCaseOutputsRepository, times(1)).save(any(GasCaseOutput.class));
  }

  @Test
  void getGasCaseOutput_verifyMethodCall() {
    gasCaseOutputsPersistenceService.getGasCaseOutput(submissionDetail);

    verify(gasCaseOutputsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}