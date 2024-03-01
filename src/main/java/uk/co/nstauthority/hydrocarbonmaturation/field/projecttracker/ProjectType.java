package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProjectType implements Displayable {
  FIELD_DEVELOPMENT_PLAN("Field development plan (FDP)", 10),
  FIELD_DEVELOPMENT_PLAN_ADDENDUM("Field development plan addendum (FDPA)", 20),
  CESSATION_OF_PRODUCTION("Cessation of production (CoP)", 30),
  ;

  private final String displayName;
  private final int displayOrder;

  ProjectType(String displayName, int displayOrder) {
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
