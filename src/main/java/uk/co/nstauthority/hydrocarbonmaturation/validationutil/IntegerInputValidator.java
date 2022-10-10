package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.numberinput.IntegerInput;

public class IntegerInputValidator extends NumericInputValidator {
  private final IntegerInput integerInput;

  private final List<Consumer<BigInteger>> validators  = new ArrayList<>();
  private boolean isMandatory = true;
  protected BigInteger minValue = null;
  protected BigInteger maxValue = null;

  private IntegerInputValidator(Errors errors, String fieldId, String errorMessagePrefix,
                                IntegerInput integerInput) {
    super(errors, fieldId, errorMessagePrefix);
    this.integerInput = integerInput;
  }

  public static IntegerInputValidator configureValidator(Errors errors, String fieldId, String errorMessagePrefix,
                                                         IntegerInput integerInput) {
    return new IntegerInputValidator(errors, fieldId, errorMessagePrefix, integerInput);
  }

  public IntegerInputValidator overrideDefaultErrorMessage(String errorMessage) {
    useDefaultErrorMessage = false;
    overriddenErrorMessage = errorMessage;
    return this;
  }

  public IntegerInputValidator setMaxValue(BigInteger maxValue) {
    this.maxValue = maxValue;
    return this;
  }

  public IntegerInputValidator setMinValue(BigInteger minValue) {
    this.minValue = minValue;
    return this;
  }

  public IntegerInputValidator isOptional() {
    isMandatory = false;
    return this;
  }

  /**
   * To avoid errors being stacked in the error summary, the first element in the validators list will be the only one
   * displayed on screen.
   * The priority of the errors are defined by the order in which the builder methods are called.
   */
  public void validate() {
    var integerValue = integerInput.asBigInteger();

    if (integerInput.hasContent() && integerValue.isEmpty()) {
      rejectNonNumericValue();
    } else if (integerValue.isEmpty() && isMandatory) {
      rejectNullValue();
    } else {
      if (minValue != null) {
        rejectMinValue(minValue);
      }
      if (maxValue != null) {
        rejectMaxValue(maxValue);
      }
    }

    integerValue.ifPresent(this::executeValidators);
  }

  private void rejectNonNumericValue() {
    var errorMessage = "%s must be a whole number".formatted(errorMessagePrefix);

    if (!useDefaultErrorMessage) {
      errorMessage = overriddenErrorMessage;
    }
    errors.rejectValue(fieldId, "%s.notNumeric".formatted(fieldId), errorMessage);
  }

  private void rejectMinValue(BigInteger minValue) {
    validators.add(value -> {
      var errorMessage = "%s must be %s or more".formatted(errorMessagePrefix, minValue.toString());

      if (!useDefaultErrorMessage) {
        errorMessage = overriddenErrorMessage;
      }

      if (value.compareTo(minValue) < 0) {
        errors.rejectValue(fieldId, "%s.lessThanMinValue".formatted(fieldId), errorMessage);
      }
    });
  }

  private void rejectMaxValue(BigInteger maxValue) {
    validators.add(value -> {

      var errorMessage = "%s must be %s or less".formatted(errorMessagePrefix, maxValue.toString());
      if (!useDefaultErrorMessage) {
        errorMessage = overriddenErrorMessage;
      }

      if (value.compareTo(maxValue) > 0) {
        errors.rejectValue(fieldId, "%s.moreThanMaxValue".formatted(fieldId), errorMessage);
      }
    });
  }

  private void executeValidators(BigInteger value) {
    for (var validator : validators) {
      if (errors.getFieldError(fieldId) == null) {
        validator.accept(value);
      } else {
        break;
      }
    }
  }
}

