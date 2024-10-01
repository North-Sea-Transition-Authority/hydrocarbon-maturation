package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Service
public class UpdateDiscoveryFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return UpdateDiscoveryForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "discoverySelectedOption",
        "discoverySelectedOption.required", "Select a discovery");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    validate(target, errors);
  }
}
