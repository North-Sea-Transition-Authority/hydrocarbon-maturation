package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum ResourceType implements Displayable {
  PROSPECT("Prospect", "Prospects", 10),
  DISCOVERY("Discovery", "Discoveries", 20),
  FIELD("Field", "Fields", 30);

  private final String displayName;
  private final String pluralName;
  private final int displayOrder;

  ResourceType(String displayName, String pluralName, int displayOrder) {
    this.displayName = displayName;
    this.pluralName = pluralName;
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
}
