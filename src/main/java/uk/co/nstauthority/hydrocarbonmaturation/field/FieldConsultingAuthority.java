package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum FieldConsultingAuthority implements Displayable {

  ENGLISH_AUTHORITIES("English authorities", 10),
  SCOTTISH_AUTHORITIES("Scottish authorities", 20),
  NORWEGIAN_AUTHORITIES("Norwegian authorities", 30);

  private final String displayName;
  private final int displayOrder;

  FieldConsultingAuthority(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
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

  public static Map<String, String> getConsultingAuthorities() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(FieldConsultingAuthority.class);
  }
}
