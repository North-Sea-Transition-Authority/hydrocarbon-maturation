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

@ExtendWith(MockitoExtension.class)
class MixedCaseOutputsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private MixedCaseOutputsRepository mixedCaseOutputsRepository;

  private MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @BeforeEach
  void setUp() {
    mixedCaseOutputsPersistenceService = new MixedCaseOutputsPersistenceService(probabilityDistributionPersistenceService,
        mixedCaseOutputsRepository);
  }

  @Test
  void saveMixedCaseOutputs_whenGivenAForm_verifyCreatedEntity() {
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

    mixedCaseOutputsPersistenceService.saveMixedCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE,
            form.getOilInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES,
            form.getOilRecoverableResources(),
            submissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
  }

  @Test
  void saveMixedCaseOutputs_whenEmptyForm_verifyCreatedEntity() {
    var form = new MixedCaseOutputsForm();
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput());

    mixedCaseOutputsPersistenceService.saveMixedCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE,
            form.getOilInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES,
            form.getAssociatedCondensateRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE,
            form.getGasInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES,
            form.getGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES,
            form.getOilRecoverableResources(),
            submissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).save(any(MixedCaseOutput.class));
  }

  @Test
  void getMixedCaseOutput_verifyMethodCall() {
    mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail);

    verify(mixedCaseOutputsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}