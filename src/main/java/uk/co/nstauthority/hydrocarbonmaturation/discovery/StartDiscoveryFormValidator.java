package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Service
class StartDiscoveryFormValidator implements SmartValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    validate(target, errors);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return StartDiscoveryForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "prospectSelectedOption",
        "prospectSelectedOption.required",
        "Select a prospect to advise of a post-drill discovery");
  }
}
