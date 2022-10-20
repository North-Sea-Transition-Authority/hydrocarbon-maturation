package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

public class ProspectSummaryItem implements ProspectTaskListItem {

  private String itemDisplayText;
  private String actionUrl;
  private Integer displayOrder;
  private Class<? extends TaskListSection<ProspectTaskListItemType>>  taskListSection;

  public ProspectSummaryItem(ProspectTaskListItem prospectTaskListItem) {
    this.actionUrl = "#" + prospectTaskListItem.getItemDisplayText().replace(" ", "").toLowerCase();
    this.itemDisplayText = prospectTaskListItem.getItemDisplayText();
    this.taskListSection = prospectTaskListItem.getTaskListSection();
    this.displayOrder = prospectTaskListItem.getDisplayOrder();
  }

  @Override
  public String getItemDisplayText() {
    return itemDisplayText;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return null;
  }

  public String getAction() {
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
