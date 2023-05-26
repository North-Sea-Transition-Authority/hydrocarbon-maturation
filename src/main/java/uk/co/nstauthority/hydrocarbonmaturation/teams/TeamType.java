package uk.co.nstauthority.hydrocarbonmaturation.teams;

public enum TeamType {

  REGULATOR("Regulator", "Team for managing regulator users", 10),
  INDUSTRY("Industry", "Teams for managing industry users", 20),
  ONSHORE_AREA("Onshore area", "Teams for managing onshore area users", 30),
  NORTHERN_OFFSHORE_AREA("Northern North Sea and West of Shetland",
      "Teams for managing northern offshore area users", 40),
  SOUTHERN_OFFSHORE_AREA("Central North Sea, Southern North Sea and East Irish Sea",
      "Teams for managing southern offshore area users", 40);

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
