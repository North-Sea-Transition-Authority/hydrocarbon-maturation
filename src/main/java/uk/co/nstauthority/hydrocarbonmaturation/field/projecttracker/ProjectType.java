package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProjectType implements Displayable {
  OPERATOR_PROGRESSION("Operator progression", 10),
  CSR_TECHNICAL_COMMENTS_WITH_REGULATOR("CSR technical comments (with regulator)", 20),
  CSR_TECHNICAL_COMMENTS_WITH_OPERATOR("CSR technical comments (with operator)", 30),
  FDP_TECHNICAL_COMMENTS_WITH_REGULATOR("FDP technical comments (with regulator)", 40),
  FDP_TECHNICAL_COMMENTS_WITH_OPERATOR("FDP technical comments (with operator)", 50),
  AWAITING_OPERATOR_INFO("Awaiting operator info", 60),
  AWAITING_APPROVED_ENVIRONMENTAL_STATEMENT("Awaiting approved environmental statement", 70),
  CONSENT_GRANTED("Consent granted", 80),
  PROJECT_CLOSE_OUT_RECEIVED_FROM_OPERATOR("Project close out received from operator", 90),
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
