package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;

@Component
class GeneralDetailsTaskListSection implements ProspectTaskListSection {

  @Override
  public String getSectionName() {
    return GeneralDetailsController.PAGE_NAME;
  }

  @Override
  public int getDisplayOrder() {
    return 10;
  }
}
