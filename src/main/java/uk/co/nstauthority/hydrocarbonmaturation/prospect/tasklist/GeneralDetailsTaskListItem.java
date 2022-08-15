package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class GeneralDetailsTaskListItem implements ProspectTaskListItem {

  @Override
  public String getItemDisplayText() {
    return GeneralDetailsController.PAGE_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(target.submissionDetail().getId(), null));
  }

  @Override
  public int getDisplayOrder() {
    return 10;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return GeneralDetailsTaskListSection.class;
  }
}
