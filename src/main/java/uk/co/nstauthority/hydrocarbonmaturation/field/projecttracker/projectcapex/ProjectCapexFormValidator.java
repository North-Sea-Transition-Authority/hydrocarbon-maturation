package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;

@Service
class ProjectCapexFormValidator {
  boolean isValid(ProjectCapexForm form, Errors errors) {
    DateValidationUtil.validateDateIsTodayOrInThePast(
        "dateProvided",
        "date the project CAPEX was provided",
        form.getDateProvidedDay(),
        form.getDateProvidedMonth(),
        form.getDateProvidedYear(),
        errors
    );


    if (form.getTotalCapex() == null) {
      errors.rejectValue(
          "totalCapex.decimalInputValue",
          "totalCapex.required",
          "Provide the total project CAPEX"
      );
    } else {
      DecimalInputValidator.configureValidator(
              errors,
              "totalCapex.decimalInputValue",
              "Total project CAPEX",
              form.getTotalCapex())
          .setMinValue(BigDecimal.ZERO)
          .setMaxDecimalPlaces(2)
          .validate();
    }

    ValidationUtils.rejectIfEmpty(
        errors,
        "sourceOfInformation",
        "sourceOfInformation.required",
        "Select the source of information");

    return !errors.hasErrors();
  }
}
