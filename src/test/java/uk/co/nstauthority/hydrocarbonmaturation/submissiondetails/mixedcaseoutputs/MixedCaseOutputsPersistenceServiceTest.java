package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class MixedCaseOutputsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private MixedCaseOutputsRepository mixedCaseOutputsRepository;

  private MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @BeforeEach
  void setUp() {
    mixedCaseOutputsPersistenceService = new MixedCaseOutputsPersistenceService(probabilityDistributionPersistenceService,
        mixedCaseOutputsRepository);
  }

  @Test
  void saveMixedCaseOutputs_whenGivenAForm_whenProspect_verifyCreatedEntity() {
    var form = new MixedCaseOutputsForm();
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setGasRecoverableResources(probDistribution);
    form.setGasInPlace(probDistribution);
    form.setAssociatedGasRecoverableResources(probDistribution);
    form.setOilInPlace(probDistribution);
    form.setOilRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    form.setAssociatedCondensateRecoverableResources(probDistribution);
    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setHasAssociatedGasRecoverableResources(true);

    mixedCaseOutputsPersistenceService.saveMixedCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
            form.getOilInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
  }

  @Test
  void saveMixedCaseOutputs_whenGivenAForm_whenDiscovery_verifyCreatedEntity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var form = new MixedCaseOutputsForm();
    var probDistribution = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setGasRecoverableResources(probDistribution);
    form.setGasInPlace(probDistribution);
    form.setAssociatedGasRecoverableResources(probDistribution);
    form.setOilInPlace(probDistribution);
    form.setOilRecoverableResources(probDistribution);
    form.setOilEquivalentRecoverableResources(probDistribution);
    form.setAssociatedCondensateRecoverableResources(probDistribution);
    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setHasAssociatedGasRecoverableResources(true);

    mixedCaseOutputsPersistenceService.saveMixedCaseOutputs(form, discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY,
            form.getOilInPlace(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilEquivalentRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedCondensateRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getAssociatedGasRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY,
            form.getGasInPlace(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getGasRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            form.getOilRecoverableResources(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
  }

  @Test
  void saveMixedCaseOutputs_whenEmptyForm_verifyCreatedEntity() {
    var form = new MixedCaseOutputsForm();
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput());

    mixedCaseOutputsPersistenceService.saveMixedCaseOutputs(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
            form.getOilInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
            form.getGasInPlace(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            form.getGasRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            form.getOilRecoverableResources(),
            submissionDetail,
            reservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
  }

  @Test
  void getMixedCaseOutput_verifyMethodCall() {
    mixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}