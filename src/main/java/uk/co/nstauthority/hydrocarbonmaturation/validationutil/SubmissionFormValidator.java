package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public interface SubmissionFormValidator {

  void validatePartial(SubmissionForm form, Errors errors);

  void validateFull(SubmissionForm form, Errors errors);

  default void validatePartialWithSubmissionDetail(SubmissionForm form, SubmissionDetail submissionDetail, Errors errors) {
    validatePartial(form, errors);
  }

  default void validateFullWithSubmissionDetail(SubmissionForm form, SubmissionDetail submissionDetail, Errors errors) {
    validateFull(form, errors);
  }
}
