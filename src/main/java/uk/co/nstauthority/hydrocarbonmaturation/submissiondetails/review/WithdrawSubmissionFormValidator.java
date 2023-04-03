package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class WithdrawSubmissionFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return WithdrawSubmissionForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "withdrawReason",
        "withdrawReason.required",
        "Enter the reason for withdrawal");
  }
}
