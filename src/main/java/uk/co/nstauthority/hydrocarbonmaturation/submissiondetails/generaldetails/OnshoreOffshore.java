package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

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
    return Arrays.stream(OnshoreOffshore.values())
        .sorted(Comparator.comparing(OnshoreOffshore::getDisplayOrder))
        .collect(Collectors.toMap(
            OnshoreOffshore::name,
            OnshoreOffshore::getDisplayName,
            (v1, v2) -> v1, LinkedHashMap::new));
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }
}
