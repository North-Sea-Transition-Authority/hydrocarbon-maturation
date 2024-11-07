package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProjectPhase implements Displayable {
  ASSESSMENT_PHASE("Assessment phase", 10),
  AUTHORISE_PHASE("Authorise phase", 20),
  EXECUTE_PHASE("Execute phase", 30),
  ON_HOLD("On hold", 40),
  ONLINE("Online", 50),
  WITHDRAWN("Withdrawn", 60)
  ;

  private final String displayName;
  private final int displayOrder;

  ProjectPhase(String displayName, int displayOrder) {
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
