package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum GeographicalArea implements Displayable {
  NORTHERN_NORTH_SEA("Northern North Sea (NNS)", 10),
  CENTRAL_NORTH_SEA("Central North Sea (CNS)", 20),
  SOUTHERN_NORTH_SEA("Southern North Sea (SNS)", 30),
  WEST_OF_SHETLAND("West of Shetland (WoS)", 40),
  EAST_IRISH_SEA("East Irish Sea", 50),
  ONSHORE("Onshore", 60);

  private final String displayName;
  private final int displayOrder;

  GeographicalArea(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}
