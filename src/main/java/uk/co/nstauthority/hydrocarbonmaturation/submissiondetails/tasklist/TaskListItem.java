package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

public record TaskListItem(
    String displayName,
    TaskListLabel label,
    String actionUrl) {

  public TaskListItem(
      String displayName,
      String actionUrl) {
    this(displayName, null, actionUrl);
  }
}