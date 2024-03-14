package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import java.time.LocalDate;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
class ProjectStageFormValidator {
  private final ProjectStageService projectStageService;

  @Autowired
  ProjectStageFormValidator(ProjectStageService projectStageService) {
    this.projectStageService = projectStageService;
  }

  boolean isValid(ProjectStageForm form, Errors errors, Project project) {
    validateFormValues(form, errors);

    if (ObjectUtils.allNull(
        errors.getFieldError("stageDateDay"),
        errors.getFieldError("stageDateMonth"),
        errors.getFieldError("stageDateYear"))) {
      // only validate when date is valid format
      validateUniqueDate(form, errors, project);
    }

    return !errors.hasErrors();
  }

  private void validateUniqueDate(ProjectStageForm form, Errors errors, Project project) {
    var newStageDate = LocalDate.of(
        Integer.parseInt(form.getStageDateYear()),
        Integer.parseInt(form.getStageDateMonth()),
        Integer.parseInt(form.getStageDateDay())
    );

    var isAlreadyStageOnDate = projectStageService.getOrderedProjectStages(project)
        .stream()
        .anyMatch(existingStageHistory -> existingStageHistory.getStageDate().equals(newStageDate));

    if (isAlreadyStageOnDate) {
      errors.rejectValue(
          "stageDateDay",
          "stageDate.duplicate",
          "There is already a project stage for the date provided"
      );
    }
  }

  private static void validateFormValues(ProjectStageForm form, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stage", "stage.required", "Select a project stage");

    DateValidationUtil.validateDateIsTodayOrInThePast(
        "stageDate",
        "date the project entered stage",
        form.getStageDateDay(),
        form.getStageDateMonth(),
        form.getStageDateYear(),
        errors
    );
  }
}
