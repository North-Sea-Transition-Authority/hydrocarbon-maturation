package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class ProspectRelinquishmentFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return ProspectRelinquishmentForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "prospectSelectedOption",
        "prospectSelectedOption.required", "Select a prospect for relinquishment");
  }
}
