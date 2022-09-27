package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

public class VolumetricsTaskListSection implements TaskListSection {

  static final String SECTION_NAME = "Volumetrics";

  static final int SECTION_DISPLAY_ORDER = 40;


  @Override
  public String getSectionName() {
    return SECTION_NAME;
  }

  @Override
  public int getDisplayOrder() {
    return SECTION_DISPLAY_ORDER;
  }
}
