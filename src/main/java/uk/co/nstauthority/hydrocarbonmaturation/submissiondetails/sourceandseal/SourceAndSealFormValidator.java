package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class SourceAndSealFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return SourceAndSealForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceFormationName", "sourceFormationName.required",
        "Enter a source formation name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealFormationName", "topSealFormationName.required",
        "Enter a top seal formation name");
  }
}
