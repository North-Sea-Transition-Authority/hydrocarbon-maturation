package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum Fault implements Displayable {
  RESERVOIR("Reservoir", 10),
  OVERBURDEN("Overburden", 20);

  private final String displayName;
  private final int displayOrder;

  Fault(String displayName, int displayOrder) {
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
}
