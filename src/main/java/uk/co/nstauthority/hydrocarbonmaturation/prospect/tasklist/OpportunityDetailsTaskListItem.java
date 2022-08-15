package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class OpportunityDetailsTaskListItem implements ProspectTaskListItem {

  @Override
  public String getItemDisplayText() {
    return OpportunityDetailsController.PAGE_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(OpportunityDetailsController.class)
        .getOpportunityDetails(target.submissionDetail().getId(), null));
  }

  @Override
  public int getDisplayOrder() {
    return 10;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return OpportunityTaskListSection.class;
  }
}
