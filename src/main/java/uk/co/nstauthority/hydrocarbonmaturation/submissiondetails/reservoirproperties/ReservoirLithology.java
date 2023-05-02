package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum ReservoirLithology implements Displayable {

  SANDSTONE("Sandstone", 10),
  CARBONATE("Carbonate", 20),
  CHALK("Chalk", 30),
  FRACTURED_BASEMENT("Fractured basement", 40),
  GRANITE("Granite", 50),
  OTHER("Other", 60);

  private final String displayName;
  private final int displayOrder;

  ReservoirLithology(String displayName, int displayOrder) {
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

  public static Map<String, String> getReservoirLithologyOptions() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirLithology.class);
  }
}
