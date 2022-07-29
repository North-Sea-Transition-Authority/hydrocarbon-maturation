package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.beans.Introspector;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class DecimalInputValidator {

  public DecimalInputValidator() {
  }

  public static class DecimalInputValidatorInvocationBuilder {
    private final Errors errors;
    private final List<Consumer<Double>> validators;
    private final String fieldId;
    private final String errorMessagePrefix;
    private final DecimalInput decimalInput;

    public DecimalInputValidatorInvocationBuilder(Errors errors, String fieldId, String errorMessagePrefix,
                                                  DecimalInput decimalInput) {
      this.errors = errors;
      this.decimalInput = decimalInput;
      validators = new ArrayList<>();
      this.fieldId = fieldId;
      this.errorMessagePrefix = errorMessagePrefix;
    }

    public DecimalInputValidatorInvocationBuilder setMaxDecimalPlaces(int decimalPlacesExpected) {
      validators.add((value) -> {
        var bigDecimal = BigDecimal.valueOf(value);
        if (bigDecimal.scale() > decimalPlacesExpected) {
          errors.rejectValue(fieldId, "%s.incorrectDecimalPlaces".formatted(fieldId),
              "%s must have no more than %d decimal places".formatted(errorMessagePrefix, decimalPlacesExpected));
        }
      });
      return this;
    }

    public DecimalInputValidatorInvocationBuilder setMaxValue(int maxValue) {
      validators.add((value) -> {
        if (value > maxValue) {
          errors.rejectValue(fieldId, "%s.moreThanMaxValue".formatted(fieldId),
              "%s must be %d or fewer".formatted(errorMessagePrefix, maxValue));
        }
      });
      return this;
    }

    public DecimalInputValidatorInvocationBuilder setMinValue(int minValue) {
      validators.add((value) -> {
        if (value < minValue) {
          errors.rejectValue(fieldId, "%s.lessThanMinValue".formatted(fieldId),
              "%s must be %d or more".formatted(errorMessagePrefix, minValue));
        }
      });
      return this;
    }

    /**
     * To avoid errors being stacked in the error summary, the first element in the validators list will be the only one
     * displayed on screen.
     * The priority of the errors are defined by the order in which the builder methods are called.
     */
    public void validate() {
      try {
        if (decimalInput.getDecimalInputValue().isEmpty()) {
          throw new NullPointerException();
        }
        var doubleValue = Double.parseDouble(decimalInput.getDecimalInputValue());
        executeValidators(doubleValue);
      } catch (NumberFormatException e) {
        errors.rejectValue(fieldId, "%s.notNumeric".formatted(fieldId), "%s must be a number".formatted(
            errorMessagePrefix));
      } catch (NullPointerException e) {
        mustNotBeNullOrEmpty();
      }
    }

    private void mustNotBeNullOrEmpty() {
      var errorMessage = Introspector.decapitalize(errorMessagePrefix);
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldId, "%s.required".formatted(fieldId),
          "Enter %s".formatted(errorMessage));
    }

    private void executeValidators(double doubleValue) {
      for (var validator : validators) {
        if (errors.getFieldError(fieldId) == null) {
          validator.accept(doubleValue);
        } else {
          break;
        }
      }
    }
  }
}
