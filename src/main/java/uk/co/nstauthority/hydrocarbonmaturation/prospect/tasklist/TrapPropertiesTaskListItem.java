package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class TrapPropertiesTaskListItem implements ProspectTaskListItem {

  @Override
  public String getItemDisplayText() {
    return TrapPropertiesController.PAGE_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(TrapPropertiesController.class)
        .getTrapProperties(target.submissionDetail().getId()));
  }

  @Override
  public int getDisplayOrder() {
    return 20;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return OpportunityTaskListSection.class;
  }
}
