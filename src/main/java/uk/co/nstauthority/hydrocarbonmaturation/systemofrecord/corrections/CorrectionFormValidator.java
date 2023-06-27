package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Service
public class CorrectionFormValidator {

  public void validate(CorrectionForm form, Errors errors) {

    ValidationUtils.rejectIfEmpty(
        errors, "correctionType", "correctionType.required", "Select what you want to correct");

    if (form.getCorrectionType() == CorrectionType.SUBMISSION_CORRECTION) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "submissionIdForCorrectingInformation",
          "submissionIdForCorrectingInformation.required",
          "Select the submission you want to correct");
    }
  }
}
