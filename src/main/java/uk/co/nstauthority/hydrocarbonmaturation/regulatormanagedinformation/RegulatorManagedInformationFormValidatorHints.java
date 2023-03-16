package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

public class RegulatorManagedInformationFormValidatorHints {
  private final boolean isResourceBeingRelinquished;

  RegulatorManagedInformationFormValidatorHints(boolean isResourceBeingRelinquished) {
    this.isResourceBeingRelinquished = isResourceBeingRelinquished;
  }

  public boolean isResourceBeingRelinquished() {
    return isResourceBeingRelinquished;
  }
}
