package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;

@ExtendWith(MockitoExtension.class)
class FieldCombinedCaseVolumetricTotalsServiceTest {
  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository;

  @InjectMocks
  private FieldCombinedCaseVolumetricTotalsService fieldCombinedCaseVolumetricTotalsService;

  @Test
  void submissionDetailToForm_noCombinedVolumetricExists() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    fieldCombinedCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);

    var hasAssociatedCondensate =
        fieldCombinedCaseVolumetricTotalsService
            .submissionDetailToForm(submissionDetail).getHasAssociatedCondensateRecoverableResources();

    assertThat(hasAssociatedCondensate).isNull();
  }

  @Test
  void submissionDetailToForm_combinedVolumetricExists() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var combinedCaseVolumetricTotal = mock(CombinedCaseVolumetricTotal.class);
    when(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(true);

    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(combinedCaseVolumetricTotal));

    fieldCombinedCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);

    var hasAssociatedCondensate =
        fieldCombinedCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)
            .getHasAssociatedCondensateRecoverableResources();

    assertThat(hasAssociatedCondensate)
        .isEqualTo(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources());
  }

  @Test
  void testSubmissionDetailToForm_isNotConceptSelectReportSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var expectedForm = createFormWithReserves();
    mockFormWithReserves(expectedForm, submissionDetail);

    assertThat(fieldCombinedCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(expectedForm);
  }

  @Test
  void testSubmissionDetailToForm_isConceptSelectReportSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    var expectedForm = createFormWithNoReserves();
    mockFormWithNoReserves(expectedForm, submissionDetail);

    assertThat(fieldCombinedCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(expectedForm);
  }

  @Test
  void saveFieldCombinedCaseVolumetricTotals_isNotCsr_reservesSaved() {
    var mixedCaseOutputsForm = createFormWithReserves();
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_COP);

    fieldCombinedCaseVolumetricTotalsService.saveFieldCombinedCaseVolumetricTotals(mixedCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
            mixedCaseOutputsForm.getGasInPlace(),
            submissionDetail
        );

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
            mixedCaseOutputsForm.getOilInPlace(),
            submissionDetail
        );

    verifyResourcesSaved(mixedCaseOutputsForm, submissionDetail);
    verifyReservesSaved(mixedCaseOutputsForm, submissionDetail);

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  @Test
  void saveFieldCombinedCaseVolumetricTotals_isCsr_noReservesSaved() {
    var mixedCaseOutputsForm = createFormWithNoReserves();
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);

    fieldCombinedCaseVolumetricTotalsService.saveFieldCombinedCaseVolumetricTotals(mixedCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
            mixedCaseOutputsForm.getGasInPlace(),
            submissionDetail
        );

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
            mixedCaseOutputsForm.getOilInPlace(),
            submissionDetail
        );

    verifyResourcesSaved(mixedCaseOutputsForm, submissionDetail);

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  private MixedCaseOutputsForm createFormWithNoReserves() {
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD);
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD);

    var gasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD);
    var associatedCondensateRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD);
    var oilEquivalentRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD);
    var naturalGasLiquidsResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD);
    var salesGasResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD);
    var oilRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD);
    var associatedGasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD);

    var expectedForm = new MixedCaseOutputsForm();
    expectedForm.setGasInPlace(gasInPlace);
    expectedForm.setOilInPlace(oilInPlace);

    expectedForm.setGasRecoverableResources(gasRecoverableResources);
    expectedForm.setHasAssociatedCondensateRecoverableResources(true);
    expectedForm.setAssociatedCondensateRecoverableResources(associatedCondensateRecoverableResources);
    expectedForm.setOilEquivalentRecoverableResources(oilEquivalentRecoverableResources);
    expectedForm.setNaturalGasLiquidsResources(naturalGasLiquidsResources);
    expectedForm.setSalesGasResources(salesGasResources);
    expectedForm.setOilRecoverableResources(oilRecoverableResources);
    expectedForm.setAssociatedGasRecoverableResources(associatedGasRecoverableResources);

    return expectedForm;
  }

  private MixedCaseOutputsForm createFormWithReserves() {
    var gasRecoverableReserves= ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD);
    var associatedCondensateRecoverableReserves= ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD);
    var oilEquivalentRecoverableReserves= ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD);
    var naturalGasLiquidsReserves= ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD);
    var salesGasReserves= ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD);
    var oilRecoverableReserves= ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD);
    var associatedGasRecoverableReserves= ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD);

    var expectedForm = createFormWithNoReserves();
    expectedForm.setGasRecoverableReserves(gasRecoverableReserves);
    expectedForm.setAssociatedCondensateRecoverableReserves(associatedCondensateRecoverableReserves);
    expectedForm.setOilEquivalentRecoverableReserves(oilEquivalentRecoverableReserves);
    expectedForm.setNaturalGasLiquidsReserves(naturalGasLiquidsReserves);
    expectedForm.setSalesGasReserves(salesGasReserves);
    expectedForm.setOilRecoverableReserves(oilRecoverableReserves);
    expectedForm.setAssociatedGasRecoverableReserves(associatedGasRecoverableReserves);

    return expectedForm;
  }

  private void mockFormWithNoReserves(MixedCaseOutputsForm form, SubmissionDetail submissionDetail) {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    )).thenReturn(form.getGasInPlace());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(form.getOilInPlace());

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(form.getGasRecoverableResources());

    var combinedCaseVolumetricTotal = new CombinedCaseVolumetricTotal();
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(combinedCaseVolumetricTotal));
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(form.getAssociatedCondensateRecoverableResources());

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(form.getOilEquivalentRecoverableResources());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    )).thenReturn(form.getNaturalGasLiquidsResources());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    )).thenReturn(form.getSalesGasResources());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(form.getOilRecoverableResources());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(form.getAssociatedGasRecoverableResources());
  }

  private void mockFormWithReserves(MixedCaseOutputsForm form, SubmissionDetail submissionDetail) {
    mockFormWithNoReserves(form, submissionDetail);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(form.getGasRecoverableReserves());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(form.getAssociatedCondensateRecoverableReserves());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(form.getOilEquivalentRecoverableReserves());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    )).thenReturn(form.getNaturalGasLiquidsReserves());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    )).thenReturn(form.getSalesGasReserves());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(form.getOilRecoverableReserves());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(form.getAssociatedGasRecoverableReserves());
  }

  private void verifyResourcesSaved(MixedCaseOutputsForm mixedCaseOutputsForm, SubmissionDetail submissionDetail) {
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
            mixedCaseOutputsForm.getOilRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
            mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
            mixedCaseOutputsForm.getGasRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
            mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
            mixedCaseOutputsForm.getOilEquivalentRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
            mixedCaseOutputsForm.getNaturalGasLiquidsResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
            mixedCaseOutputsForm.getSalesGasResources(),
            submissionDetail
        );
  }

  private void verifyReservesSaved(MixedCaseOutputsForm mixedCaseOutputsForm, SubmissionDetail submissionDetail) {
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
            mixedCaseOutputsForm.getOilRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD,
            mixedCaseOutputsForm.getAssociatedGasRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
            mixedCaseOutputsForm.getGasRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
            mixedCaseOutputsForm.getAssociatedCondensateRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
            mixedCaseOutputsForm.getOilEquivalentRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
            mixedCaseOutputsForm.getNaturalGasLiquidsReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD,
            mixedCaseOutputsForm.getSalesGasReserves(),
            submissionDetail
        );
  }
}