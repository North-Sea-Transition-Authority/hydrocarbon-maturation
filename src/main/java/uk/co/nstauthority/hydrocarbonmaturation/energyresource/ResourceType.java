package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum ResourceType implements Displayable {
  PROSPECT("Prospect", "Prospects", "Prospect (or a Lead)", 10),
  DISCOVERY("Discovery", "Discoveries", "Discovery", 20),
  FIELD("Field", "Fields", "Field", 30);

  private final String displayName;
  private final String pluralName;
  private final String secondaryOptionName;
  private final int displayOrder;

  ResourceType(String displayName, String pluralName,
               String secondaryOptionName, int displayOrder) {
    this.displayName = displayName;
    this.pluralName = pluralName;
    this.secondaryOptionName = secondaryOptionName;
    this.displayOrder = displayOrder;
  }

  public static Map<String, String> getResourceTypes() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(ResourceType.class);
  }

  public String getPluralName() {
    return pluralName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public String getSecondaryOptionName() {
    return secondaryOptionName;
  }
}
