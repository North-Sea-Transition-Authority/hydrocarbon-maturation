package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProjectStatus implements Displayable {
  DRAFT_CSR("Draft CSR", 10),
  ACCEPTED_CSR("Accepted CSR", 20),
  DRAFT_FDP("Draft FDP", 30),
  ACCEPTED_FDP("Accepted FDP", 40),
  DRAFT_FDPA("Draft FDPA", 50),
  ACCEPTED_FDPA("Accepted FDPA", 60),
  UKSS("UKSS", 70)
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
