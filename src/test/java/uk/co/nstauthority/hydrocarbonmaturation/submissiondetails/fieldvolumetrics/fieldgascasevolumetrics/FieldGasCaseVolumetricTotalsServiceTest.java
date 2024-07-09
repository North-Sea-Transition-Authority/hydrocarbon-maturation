package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotalsRepository;

@ExtendWith(MockitoExtension.class)
class FieldGasCaseVolumetricTotalsServiceTest {
  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository;

  @InjectMocks
  private FieldGasCaseVolumetricTotalsService fieldGasCaseVolumetricTotalsService;

  ArgumentCaptor<GasCaseVolumetricTotal> volumetricTotalArgumentCaptor = ArgumentCaptor.forClass(GasCaseVolumetricTotal.class);

  @Test
  void submissionDetailToForm_noGasVolumetricExists() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);

    var generatedForm = fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);

    assertThat(generatedForm.getHasAssociatedCondensateRecoverableResources()).isNull();
    assertThat(generatedForm.getFieldVolumetricInformationCorrect()).isNull();
  }

  @Test
  void submissionDetailToForm_gasVolumetricExists() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var gasVolumetricTotal = mock(GasCaseVolumetricTotal.class);
    when(gasVolumetricTotal.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(true);
    when(gasVolumetricTotal.getFieldVolumetricInformationCorrect())
        .thenReturn(true);

    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(gasVolumetricTotal));

    fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);

    var generatedForm = fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);

    assertThat(generatedForm.getHasAssociatedCondensateRecoverableResources())
        .isEqualTo(gasVolumetricTotal.getHasAssociatedCondensateRecoverableResources());
    assertThat(generatedForm.getFieldVolumetricInformationCorrect())
        .isTrue();
  }

  @Test
  void submissionDetailToForm_isConceptSelectReportSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    var oilEquivalentResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );
    var associatedCondensateResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );
    var naturalGasLiquidsResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );
    var salesGasResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    )).thenReturn(gasInPlace);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(associatedCondensateResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(oilEquivalentResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    )).thenReturn(naturalGasLiquidsResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    )).thenReturn(salesGasResources);

    assertThat(fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail))
        .extracting(
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getConstant().getConstant(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getConstant().getConstant(),
            form -> form.getNaturalGasLiquidsResources().getProbabilityDistributionType(),
            form -> form.getNaturalGasLiquidsResources().getConstant().getConstant(),
            form -> form.getSalesGasResources().getProbabilityDistributionType(),
            form -> form.getSalesGasResources().getConstant().getConstant()
        )
        .containsExactly(
            gasInPlace.getProbabilityDistributionType(),
            gasInPlace.getConstant().getConstant(),
            associatedCondensateResources.getProbabilityDistributionType(),
            associatedCondensateResources.getConstant(),
            oilEquivalentResources.getProbabilityDistributionType(),
            oilEquivalentResources.getConstant().getConstant(),
            naturalGasLiquidsResources.getProbabilityDistributionType(),
            naturalGasLiquidsResources.getConstant().getConstant(),
            salesGasResources.getProbabilityDistributionType(),
            salesGasResources.getConstant().getConstant()
        );
  }

  @Test
  void submissionDetailToForm_isNotConceptSelectReportSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    )).thenReturn(gasInPlace);

    // mock resources
    var oilEquivalentResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );
    var associatedCondensateResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );
    var naturalGasLiquidsResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );
    var salesGasResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(associatedCondensateResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(oilEquivalentResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    )).thenReturn(naturalGasLiquidsResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    )).thenReturn(salesGasResources);

    // mock reserves
    var oilEquivalentReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );
    var associatedCondensateReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );
    var naturalGasLiquidsReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );
    var salesGasReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(associatedCondensateReserves);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(oilEquivalentReserves);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    )).thenReturn(naturalGasLiquidsReserves);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    )).thenReturn(salesGasReserves);

    // check all present and correct
    assertThat(fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail))
        .extracting(
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getConstant().getConstant(),

            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getConstant().getConstant(),
            form -> form.getNaturalGasLiquidsResources().getProbabilityDistributionType(),
            form -> form.getNaturalGasLiquidsResources().getConstant().getConstant(),
            form -> form.getSalesGasResources().getProbabilityDistributionType(),
            form -> form.getSalesGasResources().getConstant().getConstant(),

            form -> form.getAssociatedCondensateRecoverableReserves().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableReserves().getConstant(),
            form -> form.getOilEquivalentRecoverableReserves().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableReserves().getConstant().getConstant(),
            form -> form.getNaturalGasLiquidsReserves().getProbabilityDistributionType(),
            form -> form.getNaturalGasLiquidsReserves().getConstant().getConstant(),
            form -> form.getSalesGasReserves().getProbabilityDistributionType(),
            form -> form.getSalesGasReserves().getConstant().getConstant()
        )
        .containsExactly(
            gasInPlace.getProbabilityDistributionType(),
            gasInPlace.getConstant().getConstant(),

            associatedCondensateResources.getProbabilityDistributionType(),
            associatedCondensateResources.getConstant(),
            oilEquivalentResources.getProbabilityDistributionType(),
            oilEquivalentResources.getConstant().getConstant(),
            naturalGasLiquidsResources.getProbabilityDistributionType(),
            naturalGasLiquidsResources.getConstant().getConstant(),
            salesGasResources.getProbabilityDistributionType(),
            salesGasResources.getConstant().getConstant(),

            associatedCondensateReserves.getProbabilityDistributionType(),
            associatedCondensateReserves.getConstant(),
            oilEquivalentReserves.getProbabilityDistributionType(),
            oilEquivalentReserves.getConstant().getConstant(),
            naturalGasLiquidsReserves.getProbabilityDistributionType(),
            naturalGasLiquidsReserves.getConstant().getConstant(),
            salesGasReserves.getProbabilityDistributionType(),
            salesGasReserves.getConstant().getConstant()
        );
  }

  @Test
  void saveFieldGasCaseVolumetricTotals_hasNoAssociatedCondensate_thenClearAndNoSave() {
    var gasCaseOutputsForm = getPopulatedFormResourcesOnly();
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);
    gasCaseOutputsForm.setHasAssociatedCondensateRecoverableResources(false);

    fieldGasCaseVolumetricTotalsService.saveFieldGasCaseVolumetricTotals(gasCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(0)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
        gasCaseOutputsForm.getGasInPlace(),
        submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
            submissionDetail
        );
    verifyVolumetricTotalsSavedWithoutInformationCorrectQuestion(submissionDetail, gasCaseOutputsForm);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldGasCaseVolumetricTotals_hasAssociatedCondensate_andVolumetricCorrectQuestion_thenSave(SubmissionType submissionType) {
    var gasCaseOutputsForm = getPopulatedFormResourcesOnly();
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(submissionType);
    gasCaseOutputsForm.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseOutputsForm.setFieldVolumetricInformationCorrect(true);

    fieldGasCaseVolumetricTotalsService.saveFieldGasCaseVolumetricTotals(gasCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
        gasCaseOutputsForm.getGasInPlace(),
        submissionDetail
    );
    verify(probabilityDistributionPersistenceService, times(0))
        .deleteProbabilityDistribution(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
            submissionDetail
        );
    verifyVolumetricTotalsSavedWithInformationCorrectQuestion(submissionDetail, gasCaseOutputsForm);
  }

  @Test
  void saveFieldGasCaseVolumetricTotals_isNotCsr_reservesSaved() {
    var gasCaseOutputsForm = getPopulatedFormResourcesOnly();
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_COP);

    fieldGasCaseVolumetricTotalsService.saveFieldGasCaseVolumetricTotals(gasCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
            gasCaseOutputsForm.getGasInPlace(),
            submissionDetail
        );

    verifyResourcesSaved(gasCaseOutputsForm, submissionDetail);
    verifyReservesSaved(gasCaseOutputsForm, submissionDetail);

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  @Test
  void saveFieldGasCaseVolumetricTotals_isCsr_noReservesSaved() {
    var gasCaseOutputsForm = getPopulatedFormResourcesAndReserves();
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);

    fieldGasCaseVolumetricTotalsService.saveFieldGasCaseVolumetricTotals(gasCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
            gasCaseOutputsForm.getGasInPlace(),
            submissionDetail
        );

    verifyResourcesSaved(gasCaseOutputsForm, submissionDetail);

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  private void verifyResourcesSaved(GasCaseOutputsForm gasCaseOutputsForm, SubmissionDetail submissionDetail) {
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
            gasCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
            gasCaseOutputsForm.getOilEquivalentRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
            gasCaseOutputsForm.getNaturalGasLiquidsResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
            gasCaseOutputsForm.getSalesGasResources(),
            submissionDetail
        );
  }

  private void verifyReservesSaved(GasCaseOutputsForm gasCaseOutputsForm, SubmissionDetail submissionDetail) {
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
            gasCaseOutputsForm.getAssociatedCondensateRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
            gasCaseOutputsForm.getOilEquivalentRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
            gasCaseOutputsForm.getNaturalGasLiquidsReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD,
            gasCaseOutputsForm.getSalesGasReserves(),
            submissionDetail
        );
  }

  private GasCaseOutputsForm getPopulatedFormResourcesOnly() {
    var form = new GasCaseOutputsForm();

    // create populated resources form
    var probabilityOfExceedanceFormResources = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceFormResources.setOneC(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceFormResources.setTwoC(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceFormResources.setThreeC(DecimalInput.createDecimalInput(1));

    // set all resources
    var probabilityDistributionFormResources = new ProbabilityDistributionForm();
    probabilityDistributionFormResources.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    probabilityDistributionFormResources.setCustom(probabilityOfExceedanceFormResources);

    form.setGasInPlace(probabilityDistributionFormResources);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionFormResources);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionFormResources);
    form.setOilEquivalentRecoverableResources(probabilityDistributionFormResources);
    form.setNaturalGasLiquidsResources(probabilityDistributionFormResources);
    form.setSalesGasResources(probabilityDistributionFormResources);

    return form;
  }

  private GasCaseOutputsForm getPopulatedFormResourcesAndReserves() {
    var form = getPopulatedFormResourcesOnly();

    // create populated reserves form
    var probabilityOfExceedanceFormResources = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceFormResources.setOneP(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceFormResources.setTwoP(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceFormResources.setThreeP(DecimalInput.createDecimalInput(1));

    // set all reserves
    var probabilityDistributionFormReserves = new ProbabilityDistributionForm();
    probabilityDistributionFormReserves.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    probabilityDistributionFormReserves.setCustom(probabilityOfExceedanceFormResources);

    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionFormReserves);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionFormReserves);
    form.setNaturalGasLiquidsReserves(probabilityDistributionFormReserves);
    form.setSalesGasReserves(probabilityDistributionFormReserves);

    return form;
  }

  private void verifyVolumetricTotalsSavedWithInformationCorrectQuestion(SubmissionDetail submissionDetail, GasCaseOutputsForm form) {
    verify(gasCaseVolumetricTotalsRepository, times(1))
        .save(volumetricTotalArgumentCaptor.capture());

    assertThat(volumetricTotalArgumentCaptor.getValue())
        .extracting(
            GasCaseVolumetricTotal::getSubmissionDetail,
            GasCaseVolumetricTotal::getFieldVolumetricInformationCorrect,
            GasCaseVolumetricTotal::getHasAssociatedCondensateRecoverableResources
        )
        .containsExactly(
            submissionDetail,
            form.getFieldVolumetricInformationCorrect(),
            form.getHasAssociatedCondensateRecoverableResources()
        );
  }

  private void verifyVolumetricTotalsSavedWithoutInformationCorrectQuestion(SubmissionDetail submissionDetail, GasCaseOutputsForm form) {
    verify(gasCaseVolumetricTotalsRepository, times(1))
        .save(volumetricTotalArgumentCaptor.capture());

    assertThat(volumetricTotalArgumentCaptor.getValue())
        .extracting(
            GasCaseVolumetricTotal::getSubmissionDetail,
            GasCaseVolumetricTotal::getFieldVolumetricInformationCorrect,
            GasCaseVolumetricTotal::getHasAssociatedCondensateRecoverableResources
        )
        .containsExactly(
            submissionDetail,
            null,
            form.getHasAssociatedCondensateRecoverableResources()
        );
  }
}