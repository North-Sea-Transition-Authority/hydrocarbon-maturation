package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum InformationSource implements Displayable {
  LICENCE_APPLICATION("Licence application", 10),
  WELL_INVESTMENT_DECISION("Well investment decision", 20),
  PRE_DRILL_SUBMISSION("Pre-drill submission", 30),
  OTHER("Other", 40);

  private final String displayName;
  private final int displayOrder;

  InformationSource(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  public static Map<String, String> getInformationSource() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(InformationSource.class);
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}
