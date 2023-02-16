package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

public enum OpportunityType implements Displayable {
  LEAD("Lead", 10, true),
  PROSPECT("Prospect", 20, true),
  DRILL_READY_PROSPECT("Drill-ready prospect", 30, true),
  DRY_WELL("Dry-well", 40, false),
  DISCOVERY("Discovery", 50, false),
  OTHER("Other", 60, true);

  private final String displayName;
  private final int displayOrder;
  private final boolean isSelectable;

  OpportunityType(String displayName, int displayOrder, boolean isSelectable) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.isSelectable = isSelectable;
  }

  static Map<String, String> getSelectableOpportunityTypes() {
    return Arrays.stream(OpportunityType.class.getEnumConstants())
        .filter(OpportunityType::isSelectable)
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            Displayable::getEnumName,
            Displayable::getDisplayName
        ));
  }

  public boolean isSelectable() {
    return isSelectable;
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
