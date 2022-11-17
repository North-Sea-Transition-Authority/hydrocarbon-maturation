package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
class SubmissionSummaryFormValidator implements Validator {

  public void validate(SubmissionSummaryForm submissionSummaryForm, Errors errors, boolean submittable) {
    if (!submittable) {
      validate(submissionSummaryForm, errors);
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(SubmissionSummaryForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "completeWithoutMandatoryInformation",
        "completeWithoutMandatoryInformation.required",
        "You must indicate you want to complete this submission without providing all required information");
  }
}
