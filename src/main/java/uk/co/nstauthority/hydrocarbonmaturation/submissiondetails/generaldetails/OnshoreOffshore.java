package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum OnshoreOffshore implements Displayable {
  OFFSHORE("Offshore", 10),
  ONSHORE("Onshore", 20);

  private final String displayName;
  private final int displayOrder;

  OnshoreOffshore(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  static Map<String, String> getOnshoreOffshore() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(OnshoreOffshore.class);
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
