package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum TopSealLithology implements Displayable {
  SALT("Salt", 10),
  EVAPORITES("Evaporites (other than salt)", 20),
  SILTSTONE("Siltstone", 30),
  SHALE("Shale", 40),
  CEMENTED_SANDSTONE("Cemented sandstone", 50),
  MUDSTONE("Mudstone", 60),
  OTHER("Other", 70),
  NOT_PROVIDED("Not provided", 80);

  private final String displayName;
  private final int displayOrder;

  TopSealLithology(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  public static Map<String, String> getTopSealLithologyOptions() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(TopSealLithology.class);
  }
}
