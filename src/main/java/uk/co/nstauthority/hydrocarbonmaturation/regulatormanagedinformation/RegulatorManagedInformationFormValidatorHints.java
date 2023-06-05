package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

public class RegulatorManagedInformationFormValidatorHints {
  private final boolean isResourceBeingRelinquished;
  private final boolean isResponsibleTeamBeingUpdated;

  RegulatorManagedInformationFormValidatorHints(boolean isResourceBeingRelinquished,
                                                boolean isResponsibleTeamBeingUpdated) {
    this.isResourceBeingRelinquished = isResourceBeingRelinquished;
    this.isResponsibleTeamBeingUpdated = isResponsibleTeamBeingUpdated;
  }

  public boolean isResourceBeingRelinquished() {
    return isResourceBeingRelinquished;
  }

  public boolean isResponsibleTeamBeingUpdated() {
    return isResponsibleTeamBeingUpdated;
  }
}
