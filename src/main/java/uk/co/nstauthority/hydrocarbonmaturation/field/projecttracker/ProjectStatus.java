package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProjectStatus implements Displayable {
  ASSESSMENT_PHASE("Assessment phase", 10),
  AUTHORISE_PHASE("Authorise phase", 20),
  EXECUTE_PHASE("Execute phase", 30),
  ON_HOLD("On hold", 40),
  WITHDRAWN("Withdrawn", 50)
  ;

  private final String displayName;
  private final int displayOrder;

  ProjectStatus(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
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
