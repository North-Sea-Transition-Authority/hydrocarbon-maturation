package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum PlannedCompletionType implements Displayable {
  PERFORATED("Perforated ", 10),
  SLOTTED_LINER("Slotted liner", 20),
  SCREENED_LINER("Screened liner", 30),
  GRAVELPACK("Gravelpack", 40),
  CEMENTED_LINER("Cemented liner", 50),
  OTHER("Other", 60);

  private final String displayName;
  private final int displayOrder;

  PlannedCompletionType(String displayName, int displayOrder) {
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
