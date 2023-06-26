package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class MixedCaseOutputsFormServiceTest {

  @Mock
  private MixedCaseOutputsFormValidator formValidator;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  @InjectMocks
  private MixedCaseOutputsFormService mixedCaseOutputsFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Test
  void validate_verifyMethodCall() {
    var form = new MixedCaseOutputsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    mixedCaseOutputsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(formValidator, times(1)).validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue().getValidationType())
        .isEqualTo(ValidationType.FULL);
  }

  @Test
  void getMixedCaseOutputsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(new ProbabilityDistributionForm());

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.of(new MixedCaseOutput()));

    assertThat(mixedCaseOutputsFormService.getMixedCaseOutputsForm(submissionDetail))
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
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    assertThat(mixedCaseOutputsFormService.getMixedCaseOutputsForm(submissionDetail))
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

  @Test
  void getMixedCaseOutputsForm_existingDiscoveryEntity_thenPopulatedForm() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoverySubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoverySubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoverySubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoverySubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoverySubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoverySubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        discoverySubmissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(discoverySubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    assertThat(mixedCaseOutputsFormService.getMixedCaseOutputsForm(discoverySubmissionDetail))
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

  @Test
  void getMixedCaseOutputsForm_emptyEntityButProbDistrFilled_thenHalfFullForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT))
        .thenReturn(probabilityDistributionForm);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    assertThat(mixedCaseOutputsFormService.getMixedCaseOutputsForm(submissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getConstant(),
            form -> form.getOilEquivalentRecoverableResources().getCustom().getComments(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
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
            form -> form.getRiskedOilEquivalentRecoverableMean().asBigDecimal(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources)
        .contains(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            null,
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
            Optional.empty(),
            null
        );
  }
}