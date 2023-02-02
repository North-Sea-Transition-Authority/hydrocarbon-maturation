package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum MigrationFailureCategory implements Displayable {
  PROSPECT_NOT_ON_MIGRATION("Prospect not on migration pathway"),
  PROSPECT_TOO_FAR("Prospect too far from mature source rock"),
  NOT_KNOWN("Not known")
  ;

  private final String displayName;

  MigrationFailureCategory(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
