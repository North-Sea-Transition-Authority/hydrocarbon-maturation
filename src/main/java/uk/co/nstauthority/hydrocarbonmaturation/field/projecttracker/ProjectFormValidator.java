package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
public class ProjectFormValidator {
  public boolean isValidProjectUpdate(Project project, ProjectForm form, Errors errors) {
    validateProjectName(errors);
    if (ProjectType.FIELD_DEVELOPMENT_PLAN.equals(project.getProjectType())
        || ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM.equals(project.getProjectType())) {
      validateProjectStatus(errors);
    }
    validateIsPartOfPipeline(errors);
    if (ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM.equals(project.getProjectType())) {
      validateFdpaStartUpDate(form, errors);
    }
    return !errors.hasErrors();
  }

  public boolean isValidNewProject(Errors errors) {
    validateProjectName(errors);
    validateProjectType(errors);
    validateIsPartOfPipeline(errors);
    return !errors.hasErrors();
  }

  private void validateProjectName(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "projectName",
        "projectName.required",
        "Provide a project name");
  }

  private void validateProjectType(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "projectType",
        "projectType.required",
        "Select a project type");
  }

  private void validateProjectStatus(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "projectPhase",
        "projectPhase.required",
        "Select a project status");
  }

  private void validateIsPartOfPipeline(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "isPartOfPipeline",
        "isPartOfPipeline.required",
        "Select if this project is part of the project pipeline");
  }

  private void validateFdpaStartUpDate(ProjectForm form, Errors errors) {
    DateValidationUtil.validateDateIsTodayOrInThePast(
        "fdpaStartUpDate",
        "FDPA start up date",
        form.getFdpaStartUpDateDay(),
        form.getFdpaStartUpDateMonth(),
        form.getFdpaStartUpDateYear(),
        errors
    );
  }
}
