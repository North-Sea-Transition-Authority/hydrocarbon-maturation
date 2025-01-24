package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
class ActualProjectDateFormValidator {

  boolean isValid(ActualProjectDateForm form, Errors errors) {
    DateValidationUtil.validateDateIsTodayOrInThePast(
        "date",
        "date",
        form.getDateDay(),
        form.getDateMonth(),
        form.getDateYear(),
        errors
    );

    return !errors.hasErrors();
  }
}