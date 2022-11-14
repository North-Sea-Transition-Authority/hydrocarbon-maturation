package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

public class SubNavigationSectionItem {
  private final ProspectTaskListSection section;
  private final String action;
  private final List<TasklistSummaryItem> taskListItems;
  private final Integer order;

  public SubNavigationSectionItem(ProspectTaskListSection section) {
    this.section = section;
    this.action = "#" + section.getSectionName().replace(" ", "").toLowerCase();
    this.taskListItems = new ArrayList<>();
    this.order = section.getDisplayOrder();
  }

  public void addTaskListItem(ProspectTaskListItem prospectTaskListItem) {
    if (!taskListItems.contains(prospectTaskListItem)
        && !(prospectTaskListItem.getClass().equals(ReviewAndSubmitTaskListItem.class))) {
      var summaryItem = new TasklistSummaryItem(prospectTaskListItem);
      taskListItems.add(summaryItem);
      getTaskListItems().sort(Comparator.comparing(TasklistSummaryItem::getDisplayOrder));
    }
  }

  public String getSectionName() {
    return section.getSectionName();
  }

  public ProspectTaskListSection getSection() {
    return section;
  }

  public int getOrder() {
    return order;
  }

  public String getAction() {
    return action;
  }

  public List<TasklistSummaryItem> getTaskListItems() {
    return taskListItems;
  }
}