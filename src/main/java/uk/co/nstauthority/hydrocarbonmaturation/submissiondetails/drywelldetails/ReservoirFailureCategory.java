package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum ReservoirFailureCategory implements Displayable {
  RESERVOIR_INTERVAL_NOT_PRESENT("Reservoir interval not present"),
  RESERVOIR_INTERVAL_POOR_QUALITY("Reservoir interval but poor quality"),
  NOT_KNOWN("Not known")
  ;

  private final String displayName;

  ReservoirFailureCategory(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
