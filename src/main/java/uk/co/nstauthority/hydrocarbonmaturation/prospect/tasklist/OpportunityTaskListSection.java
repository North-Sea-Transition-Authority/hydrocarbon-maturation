package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import org.springframework.stereotype.Component;

@Component
class OpportunityTaskListSection implements ProspectTaskListSection {

  static final String SECTION_NAME = "Opportunity";
  static final int SECTION_DISPLAY_ORDER = 20;

  @Override
  public String getSectionName() {
    return SECTION_NAME;
  }

  @Override
  public int getDisplayOrder() {
    return SECTION_DISPLAY_ORDER;
  }
}
