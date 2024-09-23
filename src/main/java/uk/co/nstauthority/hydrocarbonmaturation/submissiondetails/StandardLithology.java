package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum StandardLithology implements Displayable {
  SANDSTONE("Sandstone", 10),
  SILTSTONE("Siltstone", 20),
  MUDSTONE("Mudstone", 30),
  LIMESTONE("Limestone", 40),
  DOLOMITE("Dolomite", 50),
  CHALK("Chalk", 60),
  SALT("Salt", 70),
  NON_SALT_EVAPORITE("Non-salt evaporite", 80),
  COAL("Coal", 90),
  FRACTURED_BASEMENT("Fractured Basement", 100),
  VOLCANICS("Volcanics", 110),
  OTHER("Other lithology not listed above", 120);

  private final String displayName;
  private final int displayOrder;

  StandardLithology(String displayName, int displayOrder) {
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

  public static Map<String, String> getStandardLithologyOptions() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(StandardLithology.class);
  }
}
