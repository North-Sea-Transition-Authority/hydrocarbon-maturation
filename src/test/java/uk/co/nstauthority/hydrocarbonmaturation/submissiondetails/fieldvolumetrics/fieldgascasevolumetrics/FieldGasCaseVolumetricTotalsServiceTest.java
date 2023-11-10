package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
class FieldGasCaseVolumetricTotalsServiceTest {
  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  private FieldGasCaseVolumetricTotalsService fieldGasCaseVolumetricTotalsService;

  @Test
  void submissionDetailToForm_isConceptSelectReportSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    var recoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
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
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(recoverableResources);

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
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getConstant().getConstant(),
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
            recoverableResources.getProbabilityDistributionType(),
            recoverableResources.getConstant().getConstant(),
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
    var gasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
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
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(gasRecoverableResources);

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
    var gasRecoverableReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    );
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
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(gasRecoverableReserves);

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

            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getConstant().getConstant(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getConstant().getConstant(),
            form -> form.getNaturalGasLiquidsResources().getProbabilityDistributionType(),
            form -> form.getNaturalGasLiquidsResources().getConstant().getConstant(),
            form -> form.getSalesGasResources().getProbabilityDistributionType(),
            form -> form.getSalesGasResources().getConstant().getConstant(),

            form -> form.getGasRecoverableReserves().getProbabilityDistributionType(),
            form -> form.getGasRecoverableReserves().getConstant().getConstant(),
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

            gasRecoverableResources.getProbabilityDistributionType(),
            gasRecoverableResources.getConstant().getConstant(),
            associatedCondensateResources.getProbabilityDistributionType(),
            associatedCondensateResources.getConstant(),
            oilEquivalentResources.getProbabilityDistributionType(),
            oilEquivalentResources.getConstant().getConstant(),
            naturalGasLiquidsResources.getProbabilityDistributionType(),
            naturalGasLiquidsResources.getConstant().getConstant(),
            salesGasResources.getProbabilityDistributionType(),
            salesGasResources.getConstant().getConstant(),

            gasRecoverableReserves.getProbabilityDistributionType(),
            gasRecoverableReserves.getConstant().getConstant(),
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
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
            gasCaseOutputsForm.getGasRecoverableResources(),
            submissionDetail
        );
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
            ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
            gasCaseOutputsForm.getGasRecoverableReserves(),
            submissionDetail
        );
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

    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionFormReserves);
    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionFormReserves);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionFormReserves);
    form.setNaturalGasLiquidsReserves(probabilityDistributionFormReserves);
    form.setSalesGasReserves(probabilityDistributionFormReserves);

    return form;
  }
}