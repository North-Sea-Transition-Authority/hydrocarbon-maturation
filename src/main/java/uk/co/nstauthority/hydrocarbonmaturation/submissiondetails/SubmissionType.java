package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum SubmissionType implements Displayable {
  CREATE_PROSPECT("Prospect", "Create a new prospect", 10),
  PROSPECT_UPDATE("Prospect update", "Update an existing prospect", 20),
  POST_DRILL_DRY_HOLE("Post-drill dry hole submission", "Advise of a post-drill dry hole", 30),
  CREATE_DISCOVERY("Post-drill discovery", "Advise of a post-drill discovery", 40),
  UPDATE_DISCOVERY("Post-drill discovery update", "Update an existing discovery", 50)
  ;

  private final String typeName;
  private final String displayName;
  private final int displayOrder;

  SubmissionType(String typeName, String displayName, int displayOrder) {
    this.typeName = typeName;
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  static Map<String, String> getSubmissionTypes() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(SubmissionType.class);
  }

  public String getTypeName() {
    return typeName;
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
