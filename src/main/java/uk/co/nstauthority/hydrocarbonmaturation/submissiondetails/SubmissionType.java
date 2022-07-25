package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum SubmissionType implements Displayable {
  CREATE_PROSPECT("Create a new prospect", 10),
  PROSPECT_UPDATE("Update an existing prospect", 20);

  private final String displayName;
  private final int displayOrder;

  SubmissionType(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  static Map<String, String> getSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .sorted(Comparator.comparing(SubmissionType::getDisplayOrder))
        .collect(Collectors.toMap(
            SubmissionType::name,
            SubmissionType::getDisplayName,
            (v1, v2) -> v1, LinkedHashMap::new));
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }
}
