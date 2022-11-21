package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ReservoirDatum implements Displayable {
  WGS84("WGS84"),
  OSGB36("OSGB36"),
  ETRS89("ETRS89"),
  ED50("ED50");

  private final String display;

  ReservoirDatum(String display) {
    this.display = display;
  }

  @Override
  public String getDisplayName() {
    return display;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}
