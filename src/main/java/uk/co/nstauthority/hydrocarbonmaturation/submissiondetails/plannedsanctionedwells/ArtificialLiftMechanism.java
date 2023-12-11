package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ArtificialLiftMechanism implements Displayable {
  GAS_LIFT("Gas lift", 10),
  ELECTRIC_SUBMERSIBLE_PUMP("Electric submersible pump (ESP)", 20),
  ROD_PUMP("Rod pump", 30),
  HYDRAULIC_PUMPS("Hydraulic pumps", 40),
  PROGRESSIVE_CAVITY_PUMPS("Progressive cavity pumps", 50),
  JET_PUMPS("Jet pumps", 60),
  OTHER("Other", 70),
  NONE("There are no artifical lift mechanisms used for any of the wells", 80);

  private final String displayName;
  private final int displayOrder;

  ArtificialLiftMechanism(String displayName, int displayOrder) {
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
