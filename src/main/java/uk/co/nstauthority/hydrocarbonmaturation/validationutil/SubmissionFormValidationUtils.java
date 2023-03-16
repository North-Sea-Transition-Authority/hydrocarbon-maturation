package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import java.util.Arrays;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultFormValidatorHints;
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

  public static DrillStemTestResultFormValidatorHints extractDrillStemTestResultFormHints(Object[] hints) {
    return Arrays.stream(hints)
        .filter(DrillStemTestResultFormValidatorHints.class::isInstance)
        .map(DrillStemTestResultFormValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get DrillStemTestResultFormValidatorHints"));
  }

  public static TeamMemberEditRolesValidatorHints extractEditRolesValidatorHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(TeamMemberEditRolesValidatorHints.class::isInstance)
        .map(TeamMemberEditRolesValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get TeamMemberEditRolesValidatorHints"));
  }

  public static RegulatorManagedInformationFormValidatorHints extractRegulatorManagedInformationValidatorHints(
      Object[] validationHInts) {
    return Arrays.stream(validationHInts)
        .filter(RegulatorManagedInformationFormValidatorHints.class::isInstance)
        .map(RegulatorManagedInformationFormValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get RegulatorManagedInformationFormValidatorHints"));
  }
}
