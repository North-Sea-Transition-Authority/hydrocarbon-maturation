package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
public class CorrectionFormValidator implements Validator {
  @Override
  public boolean supports(Class<?> clazz) {
    return CorrectionForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    CorrectionForm form = (CorrectionForm) target;

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

  BindingResult validateForm(CorrectionForm form, BindingResult bindingResult) {
    validate(form, bindingResult);
    return bindingResult;
  }
}
