package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public interface SubmissionValidator {

  default boolean isValid(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail, ValidationType validationType) {
    switch (validationType) {
      case FULL -> validateFull(form, errors, submissionDetail);
      case PARTIAL -> validatePartial(form, errors, submissionDetail);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }

    return !errors.hasErrors();
  }

  void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail);

  void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail);
}