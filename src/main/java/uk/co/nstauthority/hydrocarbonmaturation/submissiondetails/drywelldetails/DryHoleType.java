package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum DryHoleType implements Displayable {
  NONE("None"),
  OIL("Oil"),
  GAS("Gas"),
  MIXED_OIL_AND_GAS("Mixed oil and gas");

  private final String display;

  DryHoleType(String display) {
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
