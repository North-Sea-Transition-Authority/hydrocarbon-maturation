package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum HydrocarbonsDiscovered implements Displayable {

  OIL_ONLY("Oil only"),
  GAS_ONLY("Gas only (including condensate)"),
  MIXED_OIL_AND_GAS("Mixed oil and gas")
  ;

  private final String displayName;

  HydrocarbonsDiscovered(String displayName) {
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
