package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum TrapType implements Displayable {
  STRUCTURAL("Structural", 10),
  STRATIGRAPHIC("Stratigraphic", 20),
  COMBINATION("Combination", 30),
  OTHER("Other", 40);

  private final String displayName;
  private final int displayOrder;

  TrapType(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  public static Map<String, String> getTrapTypes() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(TrapType.class);
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
