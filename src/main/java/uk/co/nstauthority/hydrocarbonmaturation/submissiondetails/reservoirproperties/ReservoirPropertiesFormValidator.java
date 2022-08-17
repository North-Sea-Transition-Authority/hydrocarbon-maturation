package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;

@Service
class ReservoirPropertiesFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ReservoirPropertiesForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var validationType = SubmissionFormValidationUtils.extractValidationTypeFromHints(validationHints);
    var form = (ReservoirPropertiesForm) target;

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validateAlways(SubmissionForm form, Errors errors) {

  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {

  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "formationName", "formationName.required", "Enter a formation name");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ageStageOrSeries", "ageStageOrSeries.required",
        "Enter an age stage or series");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lithology", "lithology.required", "Enter a lithology");
  }
}
