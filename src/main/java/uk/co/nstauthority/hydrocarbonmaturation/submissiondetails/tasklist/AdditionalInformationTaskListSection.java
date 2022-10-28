package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

@Component
class AdditionalInformationTaskListSection implements ProspectTaskListSection {

  static final String SECTION_NAME = AdditionalInformationController.PAGE_NAME;
  static final int SECTION_DISPLAY_ORDER = 70;

  @Override
  public String getSectionName() {
    return SECTION_NAME;
  }

  @Override
  public int getDisplayOrder() {
    return SECTION_DISPLAY_ORDER;
  }
}
