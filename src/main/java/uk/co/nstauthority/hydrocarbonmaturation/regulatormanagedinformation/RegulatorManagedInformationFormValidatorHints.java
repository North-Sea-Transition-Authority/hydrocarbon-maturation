package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

public class RegulatorManagedInformationFormValidatorHints {
  private final boolean isResourceBeingRelinquished;
  private final boolean isResponsibleTeamBeingUpdated;
  private final boolean isLinkedToShelvedCopField;
  private final boolean isAreaTeamUser;

  RegulatorManagedInformationFormValidatorHints(boolean isResourceBeingRelinquished,
                                                boolean isResponsibleTeamBeingUpdated,
                                                boolean isLinkedToShelvedCopField,
                                                boolean isAreaTeamUser) {
    this.isResourceBeingRelinquished = isResourceBeingRelinquished;
    this.isResponsibleTeamBeingUpdated = isResponsibleTeamBeingUpdated;
    this.isLinkedToShelvedCopField = isLinkedToShelvedCopField;
    this.isAreaTeamUser = isAreaTeamUser;
  }

  public boolean isResourceBeingRelinquished() {
    return isResourceBeingRelinquished;
  }

  public boolean isResponsibleTeamBeingUpdated() {
    return isResponsibleTeamBeingUpdated;
  }

  public boolean isLinkedToShelvedCopField() {
    return isLinkedToShelvedCopField;
  }

  public boolean isAreaTeamUser() {
    return isAreaTeamUser;
  }
}
