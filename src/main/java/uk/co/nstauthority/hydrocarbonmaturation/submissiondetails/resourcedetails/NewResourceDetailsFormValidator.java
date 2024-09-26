package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Service
public class NewResourceDetailsFormValidator {

  private final InternalTeamService internalTeamService;

  @Autowired
  public NewResourceDetailsFormValidator(InternalTeamService internalTeamService) {
    this.internalTeamService = internalTeamService;
  }

  public boolean isValid(Errors errors, ResourceType resourceType, User user) {
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "resourceName",
        "resourceName.required",
        "Enter the %s name".formatted(resourceType.getDisplayName().toLowerCase()));

    if (!internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors,
          "responsibleOrganisationId",
          "responsibleOrganisationId.required",
          "Enter the responsible organisation");
    }

    ValidationUtils.rejectIfEmpty(
        errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");

    return !errors.hasErrors();

  }
}
