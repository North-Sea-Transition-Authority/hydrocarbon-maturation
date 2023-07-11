package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public interface SubmissionValidator {

  void validate(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail, ValidationType validationType);
}
