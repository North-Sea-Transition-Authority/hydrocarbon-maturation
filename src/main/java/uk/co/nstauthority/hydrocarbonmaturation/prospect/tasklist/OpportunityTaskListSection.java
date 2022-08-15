package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import org.springframework.stereotype.Component;

@Component
class OpportunityTaskListSection implements ProspectTaskListSection {

  @Override
  public String getSectionName() {
    return "Opportunity";
  }

  @Override
  public int getDisplayOrder() {
    return 20;
  }
}
