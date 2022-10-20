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
  private final List<ProspectSummaryItem> prospectTaskListItems;
  private final Integer order;

  public SubNavigationSectionItem(ProspectTaskListSection section) {
    this.section = section;
    this.action = "#" + section.getSectionName().replace(" ", "").toLowerCase();
    this.prospectTaskListItems = new ArrayList<>();
    this.order = section.getDisplayOrder();
  }

  public void addTaskListItem(ProspectTaskListItem prospectTaskListItem) {
    if (!prospectTaskListItems.contains(prospectTaskListItem)
        && !(prospectTaskListItem.getClass().equals(ReviewAndSubmitTaskListItem.class))) {
      var summaryItem = new ProspectSummaryItem(prospectTaskListItem);
      prospectTaskListItems.add(summaryItem);
      prospectTaskListItems.sort(Comparator.comparing(ProspectSummaryItem::getDisplayOrder));
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

  public List<ProspectSummaryItem> getProspectTaskListItems() {
    return prospectTaskListItems;
  }
}