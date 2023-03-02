package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Service
class SubmissionSummaryFormValidator implements SmartValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var hints = extractValidatorHints(validationHints);

    if (!hints.isCompleted() && hints.isRegulator()) {
      validate(target, errors);
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

  private SubmitValidatorHints extractValidatorHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(SubmitValidatorHints.class::isInstance)
        .map(SubmitValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get SubmissionValidatorHints"));
  }
}
