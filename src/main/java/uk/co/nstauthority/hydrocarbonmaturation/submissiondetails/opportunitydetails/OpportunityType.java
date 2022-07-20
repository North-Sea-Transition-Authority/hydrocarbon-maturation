package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum OpportunityType {
  LEAD("Lead", 10),
  PROSPECT("Prospect", 20),
  DRILL_READY_PROSPECT("Drill-ready prospect", 30),
  OTHER("Other", 40);

  private final String selectionDisplayName;
  private final int selectionDisplayOrder;

  OpportunityType(String selectionDisplayName, int selectionDisplayOrder) {
    this.selectionDisplayName = selectionDisplayName;
    this.selectionDisplayOrder = selectionDisplayOrder;
  }

  public String getSelectionDisplayName() {
    return selectionDisplayName;
  }

  public int getSelectionDisplayOrder() {
    return selectionDisplayOrder;
  }

  static Map<String, String> getOpportunityType() {
    return Arrays.stream(OpportunityType.values())
        .sorted(Comparator.comparing(OpportunityType::getSelectionDisplayOrder))
        .collect(Collectors.toMap(
            OpportunityType::name,
            OpportunityType::getSelectionDisplayName,
            (v1, v2) -> v1, LinkedHashMap::new));
  }
}
