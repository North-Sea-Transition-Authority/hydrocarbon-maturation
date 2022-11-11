package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
public class DryWellDetailsTaskListItem implements PostDrillDryHoleTaskListItem {

  static final String ITEM_NAME = DryWellDetailsController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = Integer.MIN_VALUE;

  private final DryWellDetailsSubmissionService dryWellDetailsSubmissionService;

  @Autowired
  public DryWellDetailsTaskListItem(DryWellDetailsSubmissionService dryWellDetailsSubmissionService) {
    this.dryWellDetailsSubmissionService = dryWellDetailsSubmissionService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(PostDrillDryHoleTaskListItemType target) {
    return ReverseRouter.route(on(DryWellDetailsController.class)
        .getDryWellDetails(target.submissionDetail().getId(), null));
  }

  @Override
  public int getDisplayOrder() {
    return ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<PostDrillDryHoleTaskListItemType>> getTaskListSection() {
    return PostWellDrillTaskListSection.class;
  }

  @Override
  public boolean isValid(PostDrillDryHoleTaskListItemType target) {
    return dryWellDetailsSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
