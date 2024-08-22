package uk.co.nstauthority.hydrocarbonmaturation.teams;

public enum Role {
  REGULATOR_MANAGE_ALL_ORGANISATIONS("Industry access manager", "Create and edit any industry team"),
  MANAGE_TEAM("Access manager", "Add, remove and update members of this team"),
  EXPLORATION("Exploration team member", "Manage prospects and discoveries with the Exploration team"),
  VIEW_ANY_RESOURCE(
      "Viewer",
      "View all prospect, discovery and field data."),
  RESERVES_AND_RESOURCES_ADVISOR(
      "Reserves and resources advisor",
      "Manage undeveloped discoveries and NSTA's view of discovery volumes"),
  PROJECT_ANALYST(
      "Project analyst",
      "Manage field statuses and projects"),
  INDUSTRY_SUBMIT(
      "Submitter",
      "Submit updates to a prospect, discovery and fields"),
  INDUSTRY_VIEWER(
      "Viewer",
      "View all prospects, discoveries and fields relevant to my organisation"),
  MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM(
      "Area team member",
      "Manage discoveries and fields allocated to my area team");

  private final String name;

  private final String description;

  Role(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }
}
