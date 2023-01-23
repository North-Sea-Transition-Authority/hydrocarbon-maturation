package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.util.Arrays;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberEditRolesValidatorHints;

public class SubmissionFormValidationUtils {

  private SubmissionFormValidationUtils() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static SubmissionFormValidatorHints extractValidatorHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(SubmissionFormValidatorHints.class::isInstance)
        .map(SubmissionFormValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get SubmissionFormValidatorHints"));
  }

  public static TeamMemberEditRolesValidatorHints extractEditRolesValidatorHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(TeamMemberEditRolesValidatorHints.class::isInstance)
        .map(TeamMemberEditRolesValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get TeamMemberEditRolesValidatorHints"));
  }
}
