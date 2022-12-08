package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ResourceType implements Displayable {
  PROSPECT("Prospect"),
  DISCOVERY("Discovery"),
  FIELD("Field");

  private final String displayName;

  ResourceType(String displayName) {
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
