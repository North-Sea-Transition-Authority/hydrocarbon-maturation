package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class ReviewAndSubmitTaskListItem implements ProspectTaskListItem {

  static final int ITEM_DISPLAY_ORDER = 10;

  @Autowired
  ReviewAndSubmitTaskListItem() {
  }

  @Override
  public String getItemDisplayText() {
    return ReviewAndSubmitTaskListSection.SECTION_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(SubmissionSummaryController.class)
        .showSubmissionSummary(target.submissionDetail().getId()));
  }

  @Override
  public int getDisplayOrder() {
    return ReviewAndSubmitTaskListItem.ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return ReviewAndSubmitTaskListSection.class;
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return true;
  }

  @Override
  public boolean showTaskListLabels(ProspectTaskListItemType target) {
    return false;
  }
}
