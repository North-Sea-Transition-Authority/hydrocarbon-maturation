package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum HydrocarbonsDiscovered implements Displayable {

  OIL_ONLY("Oil only", "oil"),
  GAS_ONLY("Gas only (including condensate)", "gas"),
  MIXED_OIL_AND_GAS("Mixed oil and gas", "mixed oil and gas")
  ;

  private final String displayName;
  private final String inlineDisplayText;

  HydrocarbonsDiscovered(String displayName, String inlineDisplayText) {
    this.displayName = displayName;
    this.inlineDisplayText = inlineDisplayText;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }

  public String getInlineDisplayText() {
    return inlineDisplayText;
  }
}
