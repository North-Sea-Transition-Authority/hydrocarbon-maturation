package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProspectPurposeType implements Displayable {
  CREATE_PROSPECT("Prospect create", "Create a new prospect", 10),
  PROSPECT_UPDATE("Prospect update", "Update an existing prospect", 20),
  POST_DRILL_DRY_HOLE("Post-drill dry hole submission", "Advise of a post-drill dry hole", 30),
  CREATE_DISCOVERY_FROM_PROSPECT("Post-drill discovery", "Advise of a post-drill discovery", 40),
  RELINQUISHMENT_PROSPECT("Relinquishment", "Advise of a relinquishment", 50);

  private final String typeName;
  private final String displayName;
  private final int displayOrder;

  ProspectPurposeType(String typeName, String displayName, int displayOrder) {
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
