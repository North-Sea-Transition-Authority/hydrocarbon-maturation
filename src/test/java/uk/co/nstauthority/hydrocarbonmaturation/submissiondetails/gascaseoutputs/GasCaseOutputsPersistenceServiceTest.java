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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class GasCaseOutputsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private GasCaseOutputsRepository gasCaseOutputsRepository;

  private GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

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
    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(gasCaseOutputsRepository, times(1)).save(any(GasCaseOutput.class));
  }

  @Test
  void saveGasCaseOutputs_whenGivenAForm_whenDiscovery_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var form = new GasCaseOutputsForm();
    form.setGasInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedCondensateRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getGasRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            form.getGasInPlace(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedCondensateRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

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
    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(gasCaseOutputsRepository, times(1)).save(any(GasCaseOutput.class));
  }

  @Test
  void saveGasCaseOutputs_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new GasCaseOutputsForm();

    gasCaseOutputsPersistenceService.saveGasCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(gasCaseOutputsRepository, times(1)).save(any(GasCaseOutput.class));
  }

  @Test
  void getGasCaseOutput_verifyMethodCall() {
    gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail);

    verify(gasCaseOutputsRepository, times(1)).findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}