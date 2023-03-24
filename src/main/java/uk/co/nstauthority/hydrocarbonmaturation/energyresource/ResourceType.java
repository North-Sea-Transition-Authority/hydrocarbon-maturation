package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ResourceType implements Displayable {
  PROSPECT("Prospect", "Prospects"),
  DISCOVERY("Discovery", "Discoveries"),
  FIELD("Field", "Fields");

  private final String displayName;
  private final String pluralName;

  ResourceType(String displayName, String pluralName) {
    this.displayName = displayName;
    this.pluralName = pluralName;
  }

  public String getPluralName() {
    return pluralName;
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
