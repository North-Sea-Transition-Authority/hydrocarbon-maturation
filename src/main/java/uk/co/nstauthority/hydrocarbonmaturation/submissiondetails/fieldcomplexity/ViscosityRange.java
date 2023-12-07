package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ViscosityRange implements Displayable {
  ZERO_TO_ONE("0 to 1", 10, 1),
  ONE_TO_TEN("1 to 10", 20, 2),
  TEN_TO_ONE_HUNDRED("10 to 100", 30, 3),
  ONE_HUNDRED_TO_ONE_THOUSAND("100 to 1000", 40, 4),
  GREATER_THAT_ONE_THOUSAND("Greater than 1000", 50, 5);

  private final String displayName;
  private final int displayOrder;
  private final int value;

  ViscosityRange(String displayName, int displayOrder, int value) {
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
