package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum DeterminationStatus implements Displayable {
  DETERMINED("Determined", 10),
  UNDETERMINED("Undetermined", 20),
  SUB_AREA_OF_DETERMINED_FIELD("Sub area of a determined field", 30),
  ;

  private final String displayName;
  private final int displayOrder;

  DeterminationStatus(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
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
