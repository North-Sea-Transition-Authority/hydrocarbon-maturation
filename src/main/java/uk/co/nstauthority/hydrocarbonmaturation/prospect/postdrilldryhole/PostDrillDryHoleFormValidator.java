package uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Service
public class PostDrillDryHoleFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return PostDrillDryHoleForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "prospectSelectedOption", "prospectOption.required", "Select a prospect");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    validate(target, errors);
  }
}
