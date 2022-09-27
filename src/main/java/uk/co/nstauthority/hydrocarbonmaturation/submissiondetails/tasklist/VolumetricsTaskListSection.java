package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

@Component
public class VolumetricsTaskListSection implements ProspectTaskListSection {

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
