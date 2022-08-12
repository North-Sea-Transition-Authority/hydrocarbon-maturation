package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public interface SubmissionFormService {

  BindingResult validate(Object form, BindingResult bindingResult, ValidationType validationType);
}
