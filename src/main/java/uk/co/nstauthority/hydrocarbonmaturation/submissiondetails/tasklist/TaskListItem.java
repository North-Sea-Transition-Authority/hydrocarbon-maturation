package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

public record TaskListItem(
    String displayName,
    TaskListLabel label,
    String actionUrl,
    ShowOnSubNav showOnSubNav) {

  public TaskListItem(
      String displayName,
      TaskListLabel label,
      String actionUrl) {
    this(displayName, label, actionUrl, ShowOnSubNav.TRUE);
  }

  public TaskListItem(
      String displayName,
      String actionUrl,
      ShowOnSubNav showOnSubNav) {
    this(displayName, null, actionUrl, showOnSubNav);
  }
}