package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum VerticalHeterogeneityRange implements Displayable {
  ONE_TO_TEN("1 to 10", 10, 1),
  TEN_TO_ONE_HUNDRED("10 to 100", 20, 2),
  ONE_HUNDRED_TO_ONE_THOUSAND("100 to 1000", 30, 3),
  ONE_THOUSAND_TO_TEN_THOUSAND("1000 to 10,000", 40, 4),
  GREATER_THAN_TEN_THOUSAND("Greater than 10,000", 50, 5);

  private final String displayName;
  private final int displayOrder;
  private final int value;

  VerticalHeterogeneityRange(String displayName, int displayOrder, int value) {
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
