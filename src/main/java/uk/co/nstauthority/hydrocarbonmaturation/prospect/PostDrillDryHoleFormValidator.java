package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

@Service
public class PostDrillDryHoleFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return PostDrillDryHoleForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate((SubmissionForm) target, errors);
  }

  public void validate(SubmissionForm form, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "prospectSelectedOption", "prospectOption.required", "Select a prospect");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    validate((SubmissionForm) target, errors);
  }
}
