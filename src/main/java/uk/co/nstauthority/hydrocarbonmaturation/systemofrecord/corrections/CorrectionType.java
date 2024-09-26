package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum CorrectionType implements Displayable {
  HISTORIC_UPDATE("Add a missing historical update to the resource", 10),
  SUBMISSION_CORRECTION("Correct the information provided on a submission", 20),
  ACCEPTED_DATE_CHANGE("Change the accepted date for a submission", 30),
  REMOVE_SUBMISSION("Remove an incorrect submission", 40);


  private final String displayName;
  private final int displayOrder;

  CorrectionType(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  static Map<String, String> getCorrectionTypes() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(CorrectionType.class);
  }
}


