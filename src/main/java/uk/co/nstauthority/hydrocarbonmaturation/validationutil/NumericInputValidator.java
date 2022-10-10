package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.beans.Introspector;
import org.springframework.validation.Errors;

abstract class NumericInputValidator {
  protected final Errors errors;
  protected final String fieldId;
  protected final String errorMessagePrefix;
  protected boolean useDefaultErrorMessage = true;
  protected String overriddenErrorMessage;

  NumericInputValidator(Errors errors, String fieldId, String errorMessagePrefix) {
    this.errors = errors;
    this.fieldId = fieldId;
    this.errorMessagePrefix = errorMessagePrefix;
  }

  protected void rejectNullValue() {
    var errorMessage = "Enter %s".formatted(Introspector.decapitalize(errorMessagePrefix));

    if (!useDefaultErrorMessage) {
      errorMessage = overriddenErrorMessage;
    }
    errors.rejectValue(fieldId, "%s.required".formatted(fieldId), errorMessage);
  }
}
