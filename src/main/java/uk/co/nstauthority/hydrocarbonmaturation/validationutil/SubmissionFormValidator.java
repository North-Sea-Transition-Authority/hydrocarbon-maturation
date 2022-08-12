package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public interface SubmissionFormValidator {

  void validateAlways(SubmissionForm form, Errors errors);

  void validatePartial(SubmissionForm form, Errors errors);

  void validateFull(SubmissionForm form, Errors errors);

}
