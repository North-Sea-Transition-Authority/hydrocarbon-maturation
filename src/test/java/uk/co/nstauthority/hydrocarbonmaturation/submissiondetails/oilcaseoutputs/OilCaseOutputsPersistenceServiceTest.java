package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
class OilCaseOutputsPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private OilCaseOutputsRepository oilCaseOutputsRepository;

  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @BeforeEach
  void setUp() {
    oilCaseOutputsPersistenceService = new OilCaseOutputsPersistenceService(probabilityDistributionPersistenceService,
        oilCaseOutputsRepository);
  }

  @Test
  void saveOilCaseOutputs_whenGivenAForm_verifyCreatedEntity() {
    var form = new OilCaseOutputsForm();
    form.setOilInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setHasAssociatedGasRecoverableResources(true);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE,
            form.getOilInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES,
            form.getOilRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
            form.getAssociatedGasRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
  }

  @Test
  void saveOilCaseOutputs_whenGivenAFormAndOptionalFieldFalse_verifyCreatedEntity() {
    var form = new OilCaseOutputsForm();
    form.setOilInPlace(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setAssociatedGasRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setOilEquivalentRecoverableResources(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setHasAssociatedGasRecoverableResources(false);

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE,
            form.getOilInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES,
            form.getOilRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
  }

  @Test
  void saveOilCaseOutputs_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new OilCaseOutputsForm();
    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(null));

    oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE,
            form.getOilInPlace(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES,
            form.getOilRecoverableResources(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
            submissionDetail);

    verify(probabilityDistributionPersistenceService, never())
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            form.getOilEquivalentRecoverableResources(),
            submissionDetail);

    verify(oilCaseOutputsRepository, times(1)).save(any(OilCaseOutput.class));
  }
}