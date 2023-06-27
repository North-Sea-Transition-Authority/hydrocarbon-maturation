package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;

@Service
class NewFieldFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return NewFieldForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (NewFieldForm) target;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "legalFieldName", "legalFieldName.required", "Enter the legal field name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "correctedLegalFieldName", "correctedLegalFieldName.required", "Enter the corrected legal field name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "commonNameForField", "commonNameForField.required", "Enter the common name for field");

    ValidationUtils.rejectIfEmpty(errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");

    if (form.getOnshoreOffshore() == OnshoreOffshore.OFFSHORE) {
      ValidationUtils.rejectIfEmpty(errors, "geographicalArea", "geographicalArea.required", "Select a geographical area");

      ValidationUtils.rejectIfEmpty(errors, "responsibleTeamType", "responsibleTeamType.required", "Select a responsible team");
    }

    ValidationUtils.rejectIfEmpty(errors,
        "determinationStatus", "determinationStatus.required", "Select the determination status of field");

    if (form.getDeterminationStatus() == DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors,
          "parentDeterminedField", "parentDeterminedField.required", "Select the parent field");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "fieldNumber", "fieldNumber.required", "Enter the field number");

    DecimalInputValidator.configureValidator(errors,
            "fieldNumber.decimalInputValue", "Field number", form.getFieldNumber())
        .setMaxDecimalPlaces(0)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    ValidationUtils.rejectIfEmpty(errors,
        "infrastructureHost", "infrastructureHost.required", "Select yes if this field's infrastructure hosts other fields");
  }
}
