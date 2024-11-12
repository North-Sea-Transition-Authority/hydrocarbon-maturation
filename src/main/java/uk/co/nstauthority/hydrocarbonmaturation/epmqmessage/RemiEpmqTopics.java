package uk.co.nstauthority.hydrocarbonmaturation.epmqmessage;

public enum RemiEpmqTopics {
  FIELDS("remi-fields");

  private final String name;

  RemiEpmqTopics(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
