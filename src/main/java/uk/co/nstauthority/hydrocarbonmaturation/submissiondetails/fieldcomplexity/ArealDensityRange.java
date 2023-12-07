package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ArealDensityRange implements Displayable {
  LESS_THAN_ZERO_POINT_FIVE("Less than 0.5", 10, 5),
  ZERO_POINT_FIVE_TO_ONE("0.5 to 1", 20, 4),
  ONE_TO_TWO("1 to 2", 30, 3),
  TWO_TO_FOUR_POINT_FIVE("2 to 4.5", 40, 2),
  GREATER_THAN_FOUR_POINT_FIVE("Greater that 4.5", 50, 1)
  ;
  private final String displayName;
  private final int displayOrder;
  private final int value;

  ArealDensityRange(String displayName, int displayOrder, int value) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.value = value;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  public int getValue() {
    return value;
  }
}
