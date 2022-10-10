package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.numberinput.DecimalInput;

public class DecimalInputValidator extends NumericInputValidator {
  private final List<Consumer<BigDecimal>> validators  = new ArrayList<>();
  private final DecimalInput decimalInput;

  private boolean isMandatory = true;
  private BigDecimal minValue = null;
  private BigDecimal maxValue = null;
  private Integer decimalPlacesExpected = null;

  private DecimalInputValidator(Errors errors, String fieldId, String errorMessagePrefix,
                               DecimalInput decimalInput) {
    super(errors, fieldId, errorMessagePrefix);
    this.decimalInput = decimalInput;
  }

  public static DecimalInputValidator configureValidator(Errors errors, String fieldId, String errorMessagePrefix,
                                                         DecimalInput decimalInput) {
    return new DecimalInputValidator(errors, fieldId, errorMessagePrefix, decimalInput);
  }

  public DecimalInputValidator setMaxDecimalPlaces(int decimalPlacesExpected) {
    this.decimalPlacesExpected = decimalPlacesExpected;
    return this;
  }

  public DecimalInputValidator setMaxValue(BigDecimal maxValue) {
    this.maxValue = maxValue;
    return this;
  }

  public DecimalInputValidator setMinValue(BigDecimal minValue) {
    this.minValue = minValue;
    return this;
  }

  public DecimalInputValidator isOptional() {
    isMandatory = false;
    return this;
  }

  public DecimalInputValidator overrideDefaultErrorMessage(String errorMessage) {
    useDefaultErrorMessage = false;
    overriddenErrorMessage = errorMessage;
    return this;
  }

  /**
   * To avoid errors being stacked in the error summary, the first element in the validators list will be the only one
   * displayed on screen.
   * The priority of the errors are defined by the order in which the builder methods are called.
   */
  public void validate() {
    var decimalValue = decimalInput.asBigDecimal();

    if (decimalInput.hasContent() && decimalValue.isEmpty()) {
      rejectNonNumericValue();
    } else if (decimalValue.isEmpty() && isMandatory) {
      rejectNullValue();
    } else {
      if (minValue != null) {
        rejectMinValue(minValue);
      }
      if (maxValue != null) {
        rejectMaxValue(maxValue);
      }
      if (decimalPlacesExpected != null) {
        rejectDecimalPlaces(decimalPlacesExpected);
      }
    }

    decimalValue.ifPresent(this::executeValidators);
  }

  private void rejectNonNumericValue() {
    var errorMessage = "%s must be a number".formatted(errorMessagePrefix);

    if (!useDefaultErrorMessage) {
      errorMessage = overriddenErrorMessage;
    }
    errors.rejectValue(fieldId, "%s.notNumeric".formatted(fieldId), errorMessage);
  }

  private void rejectMinValue(BigDecimal minValue) {
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

  private void rejectMaxValue(BigDecimal maxValue) {
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

  private void rejectDecimalPlaces(int decimalPlacesExpected) {
    validators.add(value -> {
      var bigDecimal = value.stripTrailingZeros();
      var errorMessage = "%s must have no more than %d decimal places".formatted(errorMessagePrefix, decimalPlacesExpected);

      if (!useDefaultErrorMessage) {
        errorMessage = overriddenErrorMessage;
      } else if (decimalPlacesExpected == 0) {
        errorMessage = "%s must have no decimal places".formatted(errorMessagePrefix);
      }

      if (bigDecimal.scale() > decimalPlacesExpected) {
        errors.rejectValue(fieldId, "%s.incorrectDecimalPlaces".formatted(fieldId), errorMessage);
      }
    });
  }

  private void executeValidators(BigDecimal value) {
    for (var validator : validators) {
      if (errors.getFieldError(fieldId) == null) {
        validator.accept(value);
      } else {
        break;
      }
    }
  }
}

