package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class DiscoveryRelinquishmentFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return DiscoveryRelinquishmentForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "discoverySelectedOption",
        "discoverySelectedOption.required", "Select a discovery for relinquishment");
  }
}
