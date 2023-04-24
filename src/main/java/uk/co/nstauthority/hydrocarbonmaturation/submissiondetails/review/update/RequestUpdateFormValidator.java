package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class RequestUpdateFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(RequestUpdateForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "updateReason",
        "updateReason.required",
        "Enter reason for requesting the update");
  }
}
