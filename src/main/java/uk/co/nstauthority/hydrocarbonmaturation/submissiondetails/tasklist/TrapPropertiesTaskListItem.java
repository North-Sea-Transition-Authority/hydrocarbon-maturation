package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class TrapPropertiesTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = TrapPropertiesController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 20;

  @Override
  public String getItemDisplayText() {
    return TrapPropertiesTaskListItem.ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(TrapPropertiesController.class)
        .getTrapProperties(target.submissionDetail().getId()));
  }

  @Override
  public int getDisplayOrder() {
    return TrapPropertiesTaskListItem.ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return OpportunityTaskListSection.class;
  }
}
