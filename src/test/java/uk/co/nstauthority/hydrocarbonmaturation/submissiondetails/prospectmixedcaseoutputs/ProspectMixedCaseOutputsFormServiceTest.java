package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ProspectMixedCaseOutputsFormServiceTest {

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;

  @InjectMocks
  private ProspectMixedCaseOutputsFormService prospectMixedCaseOutputsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getMixedCaseOutputsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());

    when(prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(new MixedCaseOutput()));

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getRiskedOilEquivalentRecoverableMean().asBigDecimal(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources)
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            Optional.empty(),
            null
        );
  }

  @Test
  void getMixedCaseOutputsForm_existingProspectEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    when(prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    assertThat(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getComments(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedGasRecoverableResources().getCustom().getComments(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getCustom().getConstant(),
            form -> form.getOilInPlace().getCustom().getComments(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getConstant(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom().getComments(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getCustom().getConstant(),
            form -> form.getOilRecoverableResources().getCustom().getComments(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getCustom().getConstant(),
            form -> form.getGasInPlace().getCustom().getComments(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getCustom().getConstant(),
            form -> form.getGasRecoverableResources().getCustom().getComments(),
            form -> form.getRiskedOilEquivalentRecoverableMean().asBigDecimal().get(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources)
        .containsExactly(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            BigDecimal.ONE,
            true
        );
  }
}