package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class SourceAndSealFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return SourceAndSealForm.class.equals(clazz);
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
