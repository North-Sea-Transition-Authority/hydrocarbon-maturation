package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public abstract class PartialFullFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubmissionForm) target;
    var validationType = SubmissionFormValidationUtils
        .extractValidatorHints(validationHints)
        .getValidationType();
    var submissionDetail = SubmissionFormValidationUtils
        .extractValidatorHints(validationHints)
        .getSubmissionDetail();

    switch (validationType) {
      case FULL -> validateFullWithSubmissionDetail(form, submissionDetail, errors);
      case PARTIAL -> validatePartialWithSubmissionDetail(form, submissionDetail, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("You must provide SubmissionFormValidatorHints");
  }
}
