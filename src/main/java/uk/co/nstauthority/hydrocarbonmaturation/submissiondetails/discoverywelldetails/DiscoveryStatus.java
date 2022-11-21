package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum DiscoveryStatus implements Displayable {

  TECHNICAL_DISCOVERY("Technical discovery"),
  POTENTIALLY_COMMERCIAL_DISCOVERY("Potentially commercial discovery")
  ;

  private final String displayName;

  DiscoveryStatus(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}
