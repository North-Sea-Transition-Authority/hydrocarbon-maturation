package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

enum SubmissionType {
  CREATE_PROSPECT("Create a new prospect", 10),
  PROSPECT_UPDATE("Update an existing prospect", 20);

  private final String selectionDisplayName;
  private final int selectionDisplayOrder;

  SubmissionType(String selectionDisplayName, int selectionDisplayOrder) {
    this.selectionDisplayName = selectionDisplayName;
    this.selectionDisplayOrder = selectionDisplayOrder;
  }

  public String getSelectionDisplayName() {
    return selectionDisplayName;
  }

  public int getSelectionDisplayOrder() {
    return selectionDisplayOrder;
  }

  static Map<String, String> getSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .sorted(Comparator.comparing(SubmissionType::getSelectionDisplayOrder))
        .collect(Collectors.toMap(
            SubmissionType::name,
            SubmissionType::getSelectionDisplayName,
            (v1, v2) -> v1, LinkedHashMap::new));
  }
}
