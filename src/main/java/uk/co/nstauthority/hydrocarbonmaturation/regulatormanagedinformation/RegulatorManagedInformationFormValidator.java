package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class RegulatorManagedInformationFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return RegulatorManagedInformationForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "geographicalArea", "geographicalArea.required",
        "Select a geographical area");
  }
}
