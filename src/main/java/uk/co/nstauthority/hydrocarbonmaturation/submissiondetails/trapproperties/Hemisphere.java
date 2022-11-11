package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum Hemisphere implements Displayable {
  EAST("East", 10),
  WEST("West", 20);

  private final String displayName;
  private final int displayOrder;

  Hemisphere(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  public static Map<String, String> getLongitudeHemisphere() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(Hemisphere.class);
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
