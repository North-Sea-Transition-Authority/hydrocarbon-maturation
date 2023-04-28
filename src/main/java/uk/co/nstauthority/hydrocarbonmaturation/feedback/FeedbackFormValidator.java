package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class FeedbackFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return FeedbackForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "serviceRating",
        "serviceRating.required",
        "Select how you felt about this service");
  }
}
