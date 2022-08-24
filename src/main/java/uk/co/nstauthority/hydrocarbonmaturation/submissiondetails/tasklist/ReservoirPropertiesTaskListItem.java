package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class ReservoirPropertiesTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = ReservoirPropertiesController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 10;

  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Autowired
  ReservoirPropertiesTaskListItem(ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService) {
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(target.submissionDetail().getId()));
  }

  @Override
  public int getDisplayOrder() {
    return ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return ReservoirTaskListSection.class;
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return reservoirPropertiesSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
