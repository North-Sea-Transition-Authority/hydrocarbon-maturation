package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class ConsideringCcusFormValidator implements SubmissionValidator {
  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    // not needed
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var consideringCcusForm = (ConsideringCcusForm) form;

    validateConnectedProjects(errors, consideringCcusForm);
    validateShallowerHorizons(errors, consideringCcusForm);
  }

  private void validateConnectedProjects(Errors errors, ConsideringCcusForm form) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "isConnectedToProject",
        "isConnectedToProject.required",
        "Select if the field is hydrodynamically connected to a known carbon storage project"
    );

    if (Boolean.TRUE.equals(form.getIsConnectedToProject())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "connectedProjects",
          "connectedProjects.required",
          "Provide the connected projects"
      );
    }
  }

  private void validateShallowerHorizons(Errors errors, ConsideringCcusForm form) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "isAnyShallowerHorizons",
        "isAnyShallowerHorizons.required",
        "Select if there are any shallower horizons above the reservoir that could act as secondary " +
            "containment for a carbon storage development"
    );

    if (Boolean.TRUE.equals(form.getIsAnyShallowerHorizons())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "ageAndFormationOfHorizons",
          "ageAndFormationOfHorizons.required",
          "Provide the age and formation of all horizons that could act as secondary containment for" +
              " a carbon storage development"
      );
    }
  }
}
