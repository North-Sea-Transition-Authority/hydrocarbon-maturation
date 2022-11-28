package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public interface SubmissionFormService {

  BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType);

  default BindingResult validate(SubmissionForm form,
                                 BindingResult bindingResult,
                                 ValidationType validationType,
                                 SubmissionType submissionType) {
    return validate(form, bindingResult, validationType);
  }
}
