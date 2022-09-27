package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

class GasCaseVolumetricsTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = GasCaseVolumetricsController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 10;

  private final GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;

  @Autowired
  GasCaseVolumetricsTaskListItem(GasCaseVolumetricsTaskListItem gasCaseVolumetricsTaskListItem) {
    this.gasCaseVolumetricsSubmissionService = gasCaseVolumetricsTaskListItem;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(target.submissionDetail().getId(), null));
  }

  @Override
  public int getDisplayOrder() {
    return ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return VolumetricsTaskListSection.class;
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return generalDetailSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
