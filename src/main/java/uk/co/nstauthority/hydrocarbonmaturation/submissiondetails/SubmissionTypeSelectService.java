package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
class SubmissionTypeSelectService {
  private final SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;

  @Autowired
  SubmissionTypeSelectService(
      SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator) {
    this.submissionTypeSelectFormValidator = submissionTypeSelectFormValidator;
  }

  BindingResult validate(SubmissionTypeSelectForm form, BindingResult bindingResult) {
    submissionTypeSelectFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
