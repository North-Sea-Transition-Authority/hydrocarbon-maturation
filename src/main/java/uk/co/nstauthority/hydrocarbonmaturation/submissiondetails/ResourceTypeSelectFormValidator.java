package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Service
class ResourceTypeSelectFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return ResourceTypeSelectForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "resourceType", "resourceType.required", "Select a resource type for your submission");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    validate(target, errors);
  }
}
