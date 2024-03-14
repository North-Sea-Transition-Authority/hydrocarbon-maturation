package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
class ProjectStageFormValidator {
  boolean isValid(ProjectStageForm form, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stage", "stage.required", "Select a project stage");

    DateValidationUtil.validateDateIsTodayOrInThePast(
        "stageDate",
        "date the project entered stage",
        form.getStageDateDay(),
        form.getStageDateMonth(),
        form.getStageDateYear(),
        errors
    );

    return !errors.hasErrors();
  }
}
