package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class ReservoirPropertiesFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ReservoirPropertiesForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "formationName", "formationName.required", "Enter a formation name");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ageStageOrSeries", "ageStageOrSeries.required",
        "Enter an age stage or series");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lithology", "lithology.required", "Enter a lithology");
  }
}
