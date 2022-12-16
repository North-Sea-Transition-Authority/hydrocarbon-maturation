package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum DrillStemTestSummaryOptions implements Displayable {

  YES_NOW("Yes, I want to add it now", 10),
  YES_LATER("Yes, but I will add it later", 20),
  NO("No, I have added all the drill stem test results I need to", 30);

  private final String displayName;
  private final int displayOrder;

  DrillStemTestSummaryOptions(String displayName, int displayOrder) {
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

  @Override
  public String getEnumName() {
    return this.name();
  }
}
