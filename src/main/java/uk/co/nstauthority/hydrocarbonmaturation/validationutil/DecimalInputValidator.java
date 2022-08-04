package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.beans.Introspector;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.validation.Errors;

public class DecimalInputValidator {
  private final Errors errors;
  private final List<Consumer<BigDecimal>> validators  = new ArrayList<>();
  private final String fieldId;
  private final String errorMessagePrefix;
  private final DecimalInput decimalInput;
  private boolean isMandatory = true;

  private DecimalInputValidator(Errors errors, String fieldId, String errorMessagePrefix,
                               DecimalInput decimalInput) {
    this.errors = errors;
    this.decimalInput = decimalInput;
    this.fieldId = fieldId;
    this.errorMessagePrefix = errorMessagePrefix;
  }

  public static DecimalInputValidator configureValidator(Errors errors, String fieldId, String errorMessagePrefix,
                                                         DecimalInput decimalInput) {
    return new DecimalInputValidator(errors, fieldId, errorMessagePrefix, decimalInput);
  }

  public DecimalInputValidator setMaxDecimalPlaces(int decimalPlacesExpected) {
    validators.add((value) -> {
      if (value.scale() > decimalPlacesExpected) {
        errors.rejectValue(fieldId, "%s.incorrectDecimalPlaces".formatted(fieldId),
            "%s must have no more than %d decimal places".formatted(errorMessagePrefix, decimalPlacesExpected));
      }
    });
    return this;
  }

  public DecimalInputValidator setMaxValue(BigDecimal maxValue) {
    validators.add((value) -> {
      if (value.compareTo(maxValue) > 0) {
        errors.rejectValue(fieldId, "%s.moreThanMaxValue".formatted(fieldId),
            "%s must be %s or less".formatted(errorMessagePrefix, maxValue.toString()));
      }
    });
    return this;
  }

  public DecimalInputValidator setMinValue(BigDecimal minValue) {
    validators.add((value) -> {
      if (value.compareTo(minValue) < 0) {
        errors.rejectValue(fieldId, "%s.lessThanMinValue".formatted(fieldId),
            "%s must be %s or more".formatted(errorMessagePrefix, minValue.toString()));
      }
    });
    return this;
  }

  public DecimalInputValidator isOptional() {
    isMandatory = false;
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
      errors.rejectValue(fieldId, "%s.notNumeric".formatted(fieldId), "%s must be a number".formatted(
          errorMessagePrefix));
    } else if (decimalValue.isEmpty() && isMandatory) {
      var errorMessage = Introspector.decapitalize(errorMessagePrefix);
      errors.rejectValue(fieldId, "%s.required".formatted(fieldId), "Enter %s".formatted(errorMessage));
    }

    decimalValue.ifPresent(this::executeValidators);
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

