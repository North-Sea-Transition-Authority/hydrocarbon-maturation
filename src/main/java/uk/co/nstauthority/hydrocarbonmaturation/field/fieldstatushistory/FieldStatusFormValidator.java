package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
public class FieldStatusFormValidator implements Validator {
  @Override
  public boolean supports(@NotNull Class<?> clazz) {
    return FieldStatusForm.class.equals(clazz);
  }

  @Override
  public void validate(@NotNull Object target,
                       @NotNull Errors errors) {
    var form = (FieldStatusForm) target;

    if (form.getFieldStatus() == null) {
      errors.rejectValue(
          "fieldStatus",
          "fieldStatus.required",
          "Select a field status"
      );
    }

    DateValidationUtil.validateDate(
        "fieldStatusDate",
        "date the field entered status",
        form.getFieldStatusDateDay(),
        form.getFieldStatusDateMonth(),
        form.getFieldStatusDateYear(),
        errors
    );
  }

  public boolean isValid(@NotNull Object target,
                         @NotNull Errors errors) {
    var errorsBeforeValidation = errors.getErrorCount();
    validate(target, errors);
    return errorsBeforeValidation == errors.getErrorCount();
  }
}
