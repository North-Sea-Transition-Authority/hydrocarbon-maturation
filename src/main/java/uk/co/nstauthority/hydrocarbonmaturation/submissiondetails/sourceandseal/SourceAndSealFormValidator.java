package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class SourceAndSealFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return SourceAndSealForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SourceAndSealForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    //no fields require partial validation
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceFormationName", "sourceFormationName.required",
        "Enter a source formation name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealFormationName", "topSealFormationName.required",
        "Enter a top seal formation name");
  }
}
