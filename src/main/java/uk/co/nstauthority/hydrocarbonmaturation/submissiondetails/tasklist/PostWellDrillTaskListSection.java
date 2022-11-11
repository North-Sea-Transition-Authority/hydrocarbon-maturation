package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListSection;

@Component
public class PostWellDrillTaskListSection implements PostDrillDryHoleTaskListSection {

  static final String SECTION_NAME = "Post well drill";
  static final int SECTION_DISPLAY_ORDER = 30;

  @Override
  public String getSectionName() {
    return SECTION_NAME;
  }

  @Override
  public int getDisplayOrder() {
    return SECTION_DISPLAY_ORDER;
  }
}
