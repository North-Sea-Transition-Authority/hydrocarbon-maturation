package uk.co.nstauthority.hydrocarbonmaturation;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.TypeMismatchTestForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;

@ExtendWith(MockitoExtension.class)
class DecimalInputValidatorTest {

  @Test
  void setMaxDecimalPlaces_valid() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("12.22"))
        .setMaxDecimalPlaces(2)
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMaxDecimalPlaces_tooManyDecimalPlaces() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("12.221"))
        .setMaxDecimalPlaces(2)
        .validate();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("stringField", Set.of("stringField.incorrectDecimalPlaces"))
        );
  }

  @Test
  void setMaxValue_valid_exactlyMaxValue() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("100"))
        .setMaxValue(100)
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMaxValue_valid() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("22"))
        .setMaxValue(100)
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMaxValue_valueMoreThanMaxValue() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("101"))
        .setMaxValue(100)
        .validate();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("stringField", Set.of("stringField.moreThanMaxValue"))
        );
  }

  @Test
  void setMinValue_valid_exactlyMinValue() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("0"))
        .setMinValue(0)
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMinValue_valid() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("50"))
        .setMinValue(0)
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMinValue_valueLessThanMinValue() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("-1"))
        .setMinValue(0)
        .validate();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("stringField", Set.of("stringField.lessThanMinValue"))
        );
  }

  @Test
  void validate_valid() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("22"))
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_nullValue() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error", null)
        .validate();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("stringField", Set.of("stringField.required"))
        );
  }

  @Test
  void validate_nonNumericValue() {
    var bindingResult = getBindingResult();

    new DecimalInputValidator.DecimalInputValidatorInvocationBuilder(bindingResult, "stringField", "error",
        new DecimalInput("abcd"))
        .validate();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("stringField", Set.of("stringField.notNumeric"))
        );
  }

  private BindingResult getBindingResult() {
    var form = new TypeMismatchTestForm();
    return new BeanPropertyBindingResult(form, "form");
  }
}