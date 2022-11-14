package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

public class TasklistSummaryItem implements ProspectTaskListItem {

  private final String itemDisplayText;
  private final String actionUrl;
  private final Integer displayOrder;
  private final Class<? extends TaskListSection<ProspectTaskListItemType>>  taskListSection;

  public TasklistSummaryItem(ProspectTaskListItem taskListItem) {
    this.actionUrl = "#" + taskListItem.getItemDisplayText().replace(" ", "").toLowerCase();
    this.itemDisplayText = taskListItem.getItemDisplayText();
    this.taskListSection = taskListItem.getTaskListSection();
    this.displayOrder = taskListItem.getDisplayOrder();
  }

  @Override
  public String getItemDisplayText() {
    return itemDisplayText;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return actionUrl;
  }

  public String getActionUrl() {
    return actionUrl;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return taskListSection;
  }
}
