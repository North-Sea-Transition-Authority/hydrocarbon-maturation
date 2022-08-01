package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum OpportunityType implements Displayable {
  LEAD("Lead", 10),
  PROSPECT("Prospect", 20),
  DRILL_READY_PROSPECT("Drill-ready prospect", 30),
  OTHER("Other", 40);

  private final String displayName;
  private final int displayOrder;

  OpportunityType(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  static Map<String, String> getOpportunityType() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(OpportunityType.class);
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
