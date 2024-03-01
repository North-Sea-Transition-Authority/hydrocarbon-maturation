package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Service
public class ProjectFormValidator {

  public boolean isValid(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "projectName",
        "projectName.required",
        "Provide a project name");

    ValidationUtils.rejectIfEmpty(
        errors,
        "projectType",
        "projectType.required",
        "Select a project type");

    ValidationUtils.rejectIfEmpty(
        errors,
        "projectStatus",
        "projectStatus.required",
        "Select a project status");

    ValidationUtils.rejectIfEmpty(
        errors,
        "isPartOfPipeline",
        "isPartOfPipeline.required",
        "Select if this project is part of the project pipeline");

    return !errors.hasErrors();
  }
}
