package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum HoleCompletionType implements Displayable {
  OPENED_HOLE("Open hole", 10),
  CASED_HOLE("Cased hole", 20);

  private final String displayName;
  private final int displayOrder;

  HoleCompletionType(String displayName, int displayOrder) {
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
