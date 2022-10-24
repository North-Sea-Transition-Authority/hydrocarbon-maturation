package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
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

  private final SubmissionDetail submissionDetail = new SubmissionDetail();

  @Test
  void validate_verifyMethodCall() {
    var form = new MixedCaseOutputsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    mixedCaseOutputsFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(formValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void getMixedCaseOutputsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES))
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
  void getMixedCaseOutputsForm_existingEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);
    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    Assertions.assertThat(mixedCaseOutputsFormService.getMixedCaseOutputsForm(submissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedGasRecoverableResources().getCustom(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getCustom(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getCustom(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getCustom(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getCustom(),
            form -> form.getRiskedOilEquivalentRecoverableMean().asBigDecimal().get(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources)
        .containsExactly(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            BigDecimal.ONE,
            true
        );
  }

  @Test
  void getMixedCaseOutputsForm_emptyEntityButProbDistrFilled_thenHalfFullForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES))
        .thenReturn(probabilityDistributionForm);

    MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    assertThat(mixedCaseOutputsFormService.getMixedCaseOutputsForm(submissionDetail))
        .extracting(
            form -> form.getOilEquivalentRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilEquivalentRecoverableResources().getCustom(),
            form -> form.getAssociatedGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getProbabilityDistributionType(),
            form -> form.getOilInPlace().getCustom(),
            form -> form.getAssociatedCondensateRecoverableResources().getProbabilityDistributionType(),
            form -> form.getAssociatedCondensateRecoverableResources().getCustom(),
            form -> form.getOilRecoverableResources().getProbabilityDistributionType(),
            form -> form.getOilRecoverableResources().getCustom(),
            form -> form.getGasInPlace().getProbabilityDistributionType(),
            form -> form.getGasInPlace().getCustom(),
            form -> form.getGasRecoverableResources().getProbabilityDistributionType(),
            form -> form.getGasRecoverableResources().getCustom(),
            form -> form.getRiskedOilEquivalentRecoverableMean().asBigDecimal(),
            MixedCaseOutputsForm::getHasAssociatedGasRecoverableResources)
        .contains(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            null,
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom(),
            Optional.empty(),
            null
        );
  }
}