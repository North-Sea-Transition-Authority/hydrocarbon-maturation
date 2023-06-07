package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum DiscoveryPurposeType implements Displayable {
  CREATE_DISCOVERY_FROM_PROSPECT("Post-drill discovery", "Advise of a post-drill discovery", 10),
  CREATE_DISCOVERY("Post-drill discovery", "Create a discovery not related to a drill on a prospect", 20),
  UPDATE_DISCOVERY("Post-drill discovery update", "Update an existing discovery", 30),
  RELINQUISHMENT_DISCOVERY("Relinquishment", "Advise of a relinquishment", 40);

  private final String typeName;
  private final String displayName;
  private final int displayOrder;

  DiscoveryPurposeType(String typeName, String displayName, int displayOrder) {
    this.typeName = typeName;
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}
