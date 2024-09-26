package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Service
class StartDiscoveryFormValidator {

  public boolean isValid(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "prospectSelectedOption",
        "prospectSelectedOption.required",
        "Select a prospect to advise of a post-drill discovery");

    return !errors.hasErrors();
  }
}
