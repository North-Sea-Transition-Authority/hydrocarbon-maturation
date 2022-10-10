package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigInteger;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.TypeMismatchTestForm;
import uk.co.nstauthority.hydrocarbonmaturation.numberinput.IntegerInput;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class IntegerInputValidatorTest {

  @Test
  void setMaxValue_valid_exactlyMaxValue() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("100"))
      .setMaxValue(BigInteger.valueOf(100))
      .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMaxValue_valid() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("22"))
      .setMaxValue(BigInteger.valueOf(100))
      .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMaxValue_valueMoreThanMaxValue() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("101"))
      .setMaxValue(BigInteger.valueOf(100))
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

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("0"))
      .setMinValue(BigInteger.ZERO)
      .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMinValue_valid() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("50"))
      .setMinValue(BigInteger.ZERO)
      .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void setMinValue_valueLessThanMinValue() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("-1"))
      .setMinValue(BigInteger.ZERO)
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

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("22"))
      .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_nullValue() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error", new IntegerInput(null))
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

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
      new IntegerInput("abcd"))
      .validate();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("stringField", Set.of("stringField.notNumeric"))
        );
  }

  @Test
  void validate_isOptional_valid() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
        new IntegerInput("22"))
        .isOptional()
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_isOptional_nullValue() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
        new IntegerInput(null))
        .isOptional()
        .validate();

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_overrideErrorMessage() {
    var bindingResult = getBindingResult();

    IntegerInputValidator.configureValidator(bindingResult, "stringField", "error",
        new IntegerInput(null))
        .overrideDefaultErrorMessage("")
        .validate();

    assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEmpty();
  }

  private BindingResult getBindingResult() {
    var form = new TypeMismatchTestForm();
    return new BeanPropertyBindingResult(form, "form");
  }
}