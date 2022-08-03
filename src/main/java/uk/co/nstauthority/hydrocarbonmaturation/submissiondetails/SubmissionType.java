package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

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
    return DisplayableEnumOptionUtil.getDisplayableOptions(SubmissionType.class);
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}
