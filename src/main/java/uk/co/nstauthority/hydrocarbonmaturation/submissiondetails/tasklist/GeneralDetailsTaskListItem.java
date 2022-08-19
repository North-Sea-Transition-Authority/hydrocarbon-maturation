package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class GeneralDetailsTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = GeneralDetailsController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 10;

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(target.submissionDetail().getId(), null));
  }

  @Override
  public int getDisplayOrder() {
    return ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return GeneralDetailsTaskListSection.class;
  }
}
