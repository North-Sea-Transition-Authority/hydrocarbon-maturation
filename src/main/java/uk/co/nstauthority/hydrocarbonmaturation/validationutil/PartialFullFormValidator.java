package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public abstract class PartialFullFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubmissionForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }
}
