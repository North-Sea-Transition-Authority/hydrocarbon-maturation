package uk.co.nstauthority.hydrocarbonmaturation.teams;

public enum TeamType {

  REGULATOR("NSTA regulator", "Team for managing NSTA regulator users", 10);

  private final String displayText;
  private final String hintText;
  private final int displayOrder;

  TeamType(String displayText, String hintText, int displayOrder) {
    this.displayText = displayText;
    this.hintText = hintText;
    this.displayOrder = displayOrder;
  }

  public String getDisplayText() {
    return displayText;
  }

  public String getHintText() {
    return hintText;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

}
