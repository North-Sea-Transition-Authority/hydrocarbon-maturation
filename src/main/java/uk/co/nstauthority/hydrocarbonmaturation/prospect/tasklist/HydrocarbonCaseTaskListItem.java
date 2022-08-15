package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseController;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class HydrocarbonCaseTaskListItem implements ProspectTaskListItem {

  @Override
  public String getItemDisplayText() {
    return HydrocarbonCaseController.PAGE_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(HydrocarbonCaseController.class)
        .getHydrocarbonCase(target.submissionDetail().getId()));
  }

  @Override
  public int getDisplayOrder() {
    return 40;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return OpportunityTaskListSection.class;
  }
}
