package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class SourceAndSealFormValidator implements SubmissionValidator {

  @Override
  public boolean isValid(SubmissionForm form,
                         Errors errors,
                         SubmissionDetail submissionDetail,
                         ValidationType validationType) {
    switch (validationType) {
      case FULL -> validateFull(form, errors, submissionDetail);
      case PARTIAL -> validatePartial(form, errors, submissionDetail);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }

    return !errors.hasErrors();
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var sourceAndSealForm = (SourceAndSealForm) form;

    if (submissionDetail.isFieldSubmission()) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              "sealThickness.decimalInputValue",
              "Seal thickness",
              sourceAndSealForm.getSealThickness())
          .setMinValue(BigDecimal.ZERO)
          .setMaxDecimalPlaces(0)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "topSealFracturePressure.decimalInputValue",
              "Top seal fracture pressure",
              sourceAndSealForm.getTopSealFracturePressure())
          .setMinValue(BigDecimal.ZERO)
          .setMaxDecimalPlaces(0)
          .validate();
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    SourceAndSealForm sourceAndSealForm = (SourceAndSealForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceAgeStage", "sourceAgeStage.required",
        "Enter a source age");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceGroupName", "sourceGroupName.required",
        "Enter a source group name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceFormationName", "sourceFormationName.required",
        "Enter a source formation name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceLithology", "sourceLithology.required",
        "Enter a source lithology");

    if (sourceAndSealForm.getSourceLithology() == StandardLithology.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceLithologyOther", "sourceLithologyOther.required",
          "Enter other source lithology");
    }


    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealAgeStage", "topSealAgeStage.required",
        "Enter a top seal age");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealGroupName", "topSealGroupName.required",
        "Enter a top seal group name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealFormationName", "topSealFormationName.required",
        "Enter a top seal formation name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealLithology", "topSealLithology.required",
        "Enter a top seal lithology");

    if (sourceAndSealForm.getTopSealLithology() == StandardLithology.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topSealLithologyOther", "topSealLithologyOther.required",
          "Enter other top seal lithology");
    }

    if (submissionDetail.isFieldSubmission()) {
      DecimalInputValidator.configureValidator(
              errors,
              "sealThickness.decimalInputValue",
              "Seal thickness",
              sourceAndSealForm.getSealThickness())
          .setMinValue(BigDecimal.ZERO)
          .setMaxDecimalPlaces(0)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "topSealFracturePressure.decimalInputValue",
              "Top seal fracture pressure",
              sourceAndSealForm.getTopSealFracturePressure())
          .setMinValue(BigDecimal.ZERO)
          .setMaxDecimalPlaces(0)
          .validate();
    }
  }
}
