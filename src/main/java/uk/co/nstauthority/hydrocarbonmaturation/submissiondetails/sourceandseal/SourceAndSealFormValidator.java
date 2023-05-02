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
    SourceAndSealForm sourceAndSealForm = (SourceAndSealForm) form;
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceFormationName", "sourceFormationName.required",
        "Enter a source formation name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceAgeStage", "sourceAgeStage.required",
        "Enter a source age");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceGroupName", "sourceGroupName.required",
        "Enter a source group name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealFormationName", "topSealFormationName.required",
        "Enter a top seal formation name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealAgeStage", "topSealAgeStage.required",
        "Enter a top seal age");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealGroupName", "topSealGroupName.required",
        "Enter a top seal group name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealLithology", "topSealLithology.required",
        "Enter a top seal lithology");

    if (sourceAndSealForm.getTopSealLithology() == TopSealLithology.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealLithologyOther", "topSealLithologyOther.required",
          "Enter other top seal lithology");
    }
  }
}
