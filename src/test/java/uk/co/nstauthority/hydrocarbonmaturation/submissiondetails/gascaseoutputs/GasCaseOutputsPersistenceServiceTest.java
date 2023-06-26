package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
  void saveGasCaseOutputs_whenGivenAForm_whenProspect_verifyCreatedEntity() {
    var form = new GasCaseOutputsForm();
    form.setGasInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedCondensateRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(gasCaseOutputsRepository, times(1)).save(any(GasCaseOutput.class));
  }

  @Test
  void saveGasCaseOutputs_whenGivenAForm_whenDiscovery_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var form = new GasCaseOutputsForm();
    form.setGasInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedCondensateRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getGasRecoverableResources(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            form.getGasInPlace(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedCondensateRecoverableResources(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            discoverySubmissionDetail);

    verify(gasCaseOutputsRepository, times(1)).save(any(GasCaseOutput.class));
  }

  @Test
  void saveGasCaseOutputs_whenGivenAFormAndOptionalFieldFalse_verifyCreatedEntity() {
    var form = new GasCaseOutputsForm();
    form.setGasInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedCondensateRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedCondensateRecoverableResources(false);
    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(gasCaseOutputsRepository, times(1)).save(any(GasCaseOutput.class));
  }

  @Test
  void saveGasCaseOutputs_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new GasCaseOutputsForm();

    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
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