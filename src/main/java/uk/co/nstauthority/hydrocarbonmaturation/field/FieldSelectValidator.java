package uk.co.nstauthority.hydrocarbonmaturation.field;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Service
public class FieldSelectValidator {

  public boolean isValid(Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "fieldSelectedOption",
        "fieldSelectedOption.required", "Select a field");

    return !errors.hasErrors();
  }
}