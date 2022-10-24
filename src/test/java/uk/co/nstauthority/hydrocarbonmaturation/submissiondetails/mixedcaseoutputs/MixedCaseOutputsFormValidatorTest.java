package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class MixedCaseOutputsFormValidatorTest {

  private MixedCaseOutputsFormValidator mixedCaseOutputsFormValidator;

  @BeforeEach
  void setUp() {
    mixedCaseOutputsFormValidator = new MixedCaseOutputsFormValidator();
  }

  @Test
  void supports_whenMixedCaseOutputsFormClass_thenTrue() {
    var supportedClass = MixedCaseOutputsForm.class;
    assertThat(mixedCaseOutputsFormValidator.supports(supportedClass)).isTrue();
  }

  @Test
  void supports_whenNotMixedCaseOutputsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertThat(mixedCaseOutputsFormValidator.supports(nonSupportedClass)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    mixedCaseOutputsFormValidator.validate(form, bindingResult, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var probabilityDistributionType = ProbabilityDistributionType.CUSTOM;
    var form = getFormWithInvalidDistributions();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    mixedCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("riskedOilEquivalentRecoverableMean.decimalInputValue", Set.of("riskedOilEquivalentRecoverableMean.decimalInputValue.notNumeric"))
        );
  }

  @Test
  void validate_whenYesNoNotSelected_thenValidationErrors() {
    var form = getValidForm();
    form.setHasAssociatedGasRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    mixedCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("hasAssociatedGasRecoverableResources", Set.of("hasAssociatedGasRecoverableResources.required"))
        );
  }

  @Test
  void validate_whenEmptyExceedanceForm_thenValidationErrors() {
    var probabilityDistributionType = ProbabilityDistributionType.CUSTOM;
    var form = getFormWithEmptyDistributions();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    mixedCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("riskedOilEquivalentRecoverableMean.decimalInputValue", Set.of("riskedOilEquivalentRecoverableMean.decimalInputValue.notNumeric"))
        );
  }

  private MixedCaseOutputsForm getFormWithEmptyDistributions() {
    var form = new MixedCaseOutputsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            new ProbabilityOfExceedanceForm());
    form.setGasInPlace(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setOilInPlace(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput("abc"));
    form.setHasAssociatedGasRecoverableResources(true);

    return form;
  }

  private MixedCaseOutputsForm getFormWithInvalidDistributions() {
    var form = new MixedCaseOutputsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm()
        );
    form.setGasInPlace(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setOilInPlace(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput("abc"));
    form.setHasAssociatedGasRecoverableResources(false);

    return form;
  }

  private MixedCaseOutputsForm getValidForm() {
    var form = new MixedCaseOutputsForm();
    var probDistributionForm = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setGasInPlace(probDistributionForm);
    form.setAssociatedGasRecoverableResources(probDistributionForm);
    form.setOilEquivalentRecoverableResources(probDistributionForm);
    form.setOilRecoverableResources(probDistributionForm);
    form.setOilInPlace(probDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probDistributionForm);
    form.setGasRecoverableResources(probDistributionForm);
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput("1"));
    form.setHasAssociatedGasRecoverableResources(true);
    return form;
  }

}