package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

@Component
public class ReviewAndSubmitTaskListSection implements ProspectTaskListSection {

  static final String SECTION_NAME = "Review and submit";
  static final int SECTION_DISPLAY_ORDER = Integer.MAX_VALUE; //Make sure it's the last one

  @Override
  public String getSectionName() {
    return SECTION_NAME;
  }

  @Override
  public int getDisplayOrder() {
    return SECTION_DISPLAY_ORDER;
  }
}
