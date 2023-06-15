package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum SourceLithology implements Displayable {
  SHALE("Shale", 10),
  CLAYSTONE("Claystone", 20),
  MUDSTONE("Mudstone", 30),
  COALS("Coals", 40),
  ARGILLACEOUS_DOLOMITE("Argillaceous Dolomite", 50),
  ARGILLACEOUS_LIMESTONE("Argillaceous Limestone", 60),
  OTHER("Other", 70),
  NOT_PROVIDED("Not provided", 80);

  private final String displayName;
  private final int displayOrder;

  SourceLithology(String displayName, int displayOrder) {
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

  public static Map<String, String> getSourceLithologyOptions() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(SourceLithology.class);
  }
}
