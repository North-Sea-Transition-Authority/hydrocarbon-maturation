package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Service
public class DiscoveryPurposeTypeSelectFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return DiscoveryPurposeTypeSelectForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "purposeType", "purposeType.required", "Select a purpose for your submission");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    validate(target, errors);
  }
}
