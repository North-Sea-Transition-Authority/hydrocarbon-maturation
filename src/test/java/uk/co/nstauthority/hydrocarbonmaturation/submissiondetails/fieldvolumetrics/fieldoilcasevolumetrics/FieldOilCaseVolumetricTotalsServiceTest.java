package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotalsRepository;

@ExtendWith(MockitoExtension.class)
class FieldOilCaseVolumetricTotalsServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private OilCaseVolumetricTotalsRepository oilCaseVolumetricTotalsRepository;

  @InjectMocks
  private FieldOilCaseVolumetricTotalsService fieldOilCaseVolumetricTotalsService;

  ArgumentCaptor<OilCaseVolumetricTotal> volumetricTotalArgumentCaptor = ArgumentCaptor.forClass(
      OilCaseVolumetricTotal.class);

  @Test
  void submissionDetailToForm_isConceptSelectReportSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    var recoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );
    var oilEquivalent = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );
    var associatedCondensate = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
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
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(recoverableResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(associatedCondensate);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(oilEquivalent);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    )).thenReturn(naturalGasLiquidsResources);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    )).thenReturn(salesGasResources);

    var oilCaseVolumetricTotal = new OilCaseVolumetricTotal();
    var fieldVolumetricInformationCorrect = Boolean.TRUE;
    oilCaseVolumetricTotal.setFieldVolumetricInformationCorrect(fieldVolumetricInformationCorrect);
    when(oilCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(oilCaseVolumetricTotal));

    assertThat(fieldOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail))
        .extracting(
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getConstant().getConstant(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getConstant().getConstant(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getConstant().getConstant(),
            form -> form.getNaturalGasLiquidsResources().getProbabilityDistributionType(),
            form -> form.getNaturalGasLiquidsResources().getConstant().getConstant(),
            form -> form.getSalesGasResources().getProbabilityDistributionType(),
            form -> form.getSalesGasResources().getConstant().getConstant(),
            OilCaseOutputsForm::getFieldVolumetricInformationCorrect
        )
        .containsExactly(
            oilInPlace.getProbabilityDistributionType(),
            oilInPlace.getConstant().getConstant(),
            recoverableResources.getProbabilityDistributionType(),
            recoverableResources.getConstant().getConstant(),
            associatedCondensate.getProbabilityDistributionType(),
            associatedCondensate.getConstant(),
            oilEquivalent.getProbabilityDistributionType(),
            oilEquivalent.getConstant().getConstant(),
            naturalGasLiquidsResources.getProbabilityDistributionType(),
            naturalGasLiquidsResources.getConstant().getConstant(),
            salesGasResources.getProbabilityDistributionType(),
            salesGasResources.getConstant().getConstant(),
            fieldVolumetricInformationCorrect
        );
  }

  @Test
  void submissionDetailToForm_isNotConceptSelectReportSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    )).thenReturn(oilInPlace);

    // mock resources
    var oilRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
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
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    )).thenReturn(oilRecoverableResources);

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

    when(oilCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    // mock reserves
    var oilRecoverableReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );
    var associatedCondensateRecoverableReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );
    var oilEquivalentRecoverableReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );
    var naturalGasLiquidsReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );
    var salesGasReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(oilRecoverableReserves);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(associatedCondensateRecoverableReserves);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    )).thenReturn(oilEquivalentRecoverableReserves);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    )).thenReturn(naturalGasLiquidsReserves);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    )).thenReturn(salesGasReserves);

    // check all are present and correct
    assertThat(fieldOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail))
        .extracting(
            // Resources
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getConstant().getConstant(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getConstant().getConstant(),
            form -> form.getOilRecoverableResources().getConstant().getComments(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getConstant().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getConstant().getComments(),
            form -> form.getNaturalGasLiquidsResources().getProbabilityDistributionType(),
            form -> form.getNaturalGasLiquidsResources().getConstant().getConstant(),
            form -> form.getNaturalGasLiquidsResources().getConstant().getComments(),
            form -> form.getSalesGasResources().getProbabilityDistributionType(),
            form -> form.getSalesGasResources().getConstant().getConstant(),
            form -> form.getSalesGasResources().getConstant().getComments(),

            // Reserves
            form -> form.getOilRecoverableReserves().getProbabilityDistributionType(),
            form -> form.getOilRecoverableReserves().getConstant().getConstant(),
            form -> form.getOilRecoverableReserves().getConstant().getComments(),
            form -> form.getAssociatedCondensateRecoverableReserves().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableReserves().getConstant(),
            form -> form.getOilEquivalentRecoverableReserves().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableReserves().getConstant().getConstant(),
            form -> form.getOilEquivalentRecoverableReserves().getConstant().getComments(),
            form -> form.getNaturalGasLiquidsReserves().getProbabilityDistributionType(),
            form -> form.getNaturalGasLiquidsReserves().getConstant().getConstant(),
            form -> form.getNaturalGasLiquidsReserves().getConstant().getComments(),
            form -> form.getSalesGasReserves().getProbabilityDistributionType(),
            form -> form.getSalesGasReserves().getConstant().getConstant(),
            form -> form.getSalesGasReserves().getConstant().getComments(),
            OilCaseOutputsForm::getFieldVolumetricInformationCorrect
        )
        .contains(
            //Reserves
            oilInPlace.getProbabilityDistributionType(),
            oilInPlace.getConstant().getConstant(),
            oilRecoverableResources.getProbabilityDistributionType(),
            oilRecoverableResources.getConstant().getConstant(),
            oilRecoverableResources.getConstant().getComments(),
            associatedCondensateRecoverableReserves.getProbabilityDistributionType(),
            associatedCondensateRecoverableReserves.getConstant(),
            oilEquivalentResources.getProbabilityDistributionType(),
            oilEquivalentResources.getConstant().getConstant(),
            oilEquivalentResources.getConstant().getComments(),
            naturalGasLiquidsResources.getProbabilityDistributionType(),
            naturalGasLiquidsResources.getConstant().getConstant(),
            naturalGasLiquidsResources.getConstant().getComments(),
            salesGasResources.getProbabilityDistributionType(),
            salesGasResources.getConstant().getConstant(),
            salesGasResources.getConstant().getComments(),

            //Resources
            oilRecoverableReserves.getProbabilityDistributionType(),
            oilRecoverableReserves.getConstant().getConstant(),
            oilRecoverableReserves.getConstant().getComments(),
            associatedCondensateRecoverableReserves.getProbabilityDistributionType(),
            associatedCondensateRecoverableReserves.getConstant(),
            oilEquivalentRecoverableReserves.getProbabilityDistributionType(),
            oilEquivalentRecoverableReserves.getConstant().getConstant(),
            oilEquivalentRecoverableReserves.getConstant().getComments(),
            naturalGasLiquidsReserves.getProbabilityDistributionType(),
            naturalGasLiquidsReserves.getConstant().getConstant(),
            naturalGasLiquidsReserves.getConstant().getComments(),
            salesGasReserves.getProbabilityDistributionType(),
            salesGasReserves.getConstant().getConstant(),
            salesGasReserves.getConstant().getComments(),

            // field volumetric information correct question
            null
        );

  }

  @Test
  void saveFieldOilCaseVolumetricTotals_isNotCsr_reservesSaved() {
    var oilCaseOutputsForm = getPopulatedFormResourcesOnly();
    oilCaseOutputsForm.setFieldVolumetricInformationCorrect(true);
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);

    fieldOilCaseVolumetricTotalsService.saveFieldOilCaseVolumetricTotals(oilCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
            oilCaseOutputsForm.getOilInPlace(),
            submissionDetail
        );

    verifyResourcesSaved(oilCaseOutputsForm, submissionDetail);
    verifyVolumetricTotalsSaved(submissionDetail, oilCaseOutputsForm);
    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  @Test
  void saveFieldOilCaseVolumetricTotals_isCsr_noReservesSaved() {
    var oilCaseOutputsForm = getPopulatedFormResourcesAndReserves();
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_COP);

    fieldOilCaseVolumetricTotalsService.saveFieldOilCaseVolumetricTotals(oilCaseOutputsForm, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
            oilCaseOutputsForm.getOilInPlace(),
            submissionDetail
        );

    verifyResourcesSaved(oilCaseOutputsForm, submissionDetail);
    verifyReservesSaved(oilCaseOutputsForm, submissionDetail);

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  private void verifyResourcesSaved(OilCaseOutputsForm oilCaseOutputsForm, SubmissionDetail submissionDetail) {
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
            oilCaseOutputsForm.getOilRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
            oilCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
            oilCaseOutputsForm.getOilEquivalentRecoverableResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
            oilCaseOutputsForm.getNaturalGasLiquidsResources(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
            oilCaseOutputsForm.getSalesGasResources(),
            submissionDetail
        );
  }

  private void verifyReservesSaved(OilCaseOutputsForm oilCaseOutputsForm, SubmissionDetail submissionDetail) {
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
            oilCaseOutputsForm.getOilRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
            oilCaseOutputsForm.getAssociatedCondensateRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
            oilCaseOutputsForm.getOilEquivalentRecoverableReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
            oilCaseOutputsForm.getNaturalGasLiquidsReserves(),
            submissionDetail
        );
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD,
            oilCaseOutputsForm.getSalesGasReserves(),
            submissionDetail
        );
  }

  private OilCaseOutputsForm getPopulatedFormResourcesOnly() {
    var form = new OilCaseOutputsForm();

    // create populated resources form
    var probabilityOfExceedanceFormResources = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceFormResources.setOneC(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceFormResources.setTwoC(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceFormResources.setThreeC(DecimalInput.createDecimalInput(1));

    // set all resources
    var probabilityDistributionFormResources = new ProbabilityDistributionForm();
    probabilityDistributionFormResources.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    probabilityDistributionFormResources.setCustom(probabilityOfExceedanceFormResources);

    form.setOilInPlace(probabilityDistributionFormResources);
    form.setOilRecoverableResources(probabilityDistributionFormResources);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionFormResources);
    form.setAssociatedGasRecoverableResources(probabilityDistributionFormResources);
    form.setOilEquivalentRecoverableResources(probabilityDistributionFormResources);
    form.setNaturalGasLiquidsResources(probabilityDistributionFormResources);
    form.setSalesGasResources(probabilityDistributionFormResources);

    return form;
  }

  private OilCaseOutputsForm getPopulatedFormResourcesAndReserves() {
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
    form.setOilRecoverableReserves(probabilityDistributionFormReserves);
    form.setAssociatedGasRecoverableReserves(probabilityDistributionFormReserves);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionFormReserves);
    form.setNaturalGasLiquidsReserves(probabilityDistributionFormReserves);
    form.setSalesGasReserves(probabilityDistributionFormReserves);

    return form;
  }

  private void verifyVolumetricTotalsSaved(SubmissionDetail submissionDetail, OilCaseOutputsForm form) {
    verify(oilCaseVolumetricTotalsRepository, times(1))
        .save(volumetricTotalArgumentCaptor.capture());

    assertThat(volumetricTotalArgumentCaptor.getValue())
        .extracting(
            OilCaseVolumetricTotal::getSubmissionDetail,
            OilCaseVolumetricTotal::getFieldVolumetricInformationCorrect
        )
        .containsExactly(
            submissionDetail,
            form.getFieldVolumetricInformationCorrect()
        );
  }
}