package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class OutlineForMaximumCaseVerifyFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OutlineForMaximumCaseVerifyForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    //no fields require partial validation

  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {

    ValidationUtils.rejectIfEmpty(
        errors,
        "shapeIsCorrect",
        "shapeIsCorrect.required",
        "Select whether the outline for maximum case shape is correct"
    );
  }
}
