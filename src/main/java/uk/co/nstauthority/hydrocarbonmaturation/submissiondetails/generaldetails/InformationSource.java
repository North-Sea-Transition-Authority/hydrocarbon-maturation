package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum InformationSource implements Displayable {
  LICENCE_APPLICATION("Licence application", 10),
  WELL_INVESTMENT_DECISION("Well investment decision", 20),
  PRE_DRILL_SUBMISSION("Pre-drill submission", 30),
  OTHER("Other", 40);

  private final String displayName;
  private final int displayOrder;

  InformationSource(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  static Map<String, String> getInformationSource() {
    return Arrays.stream(InformationSource.values())
        .sorted(Comparator.comparing(InformationSource::getDisplayOrder))
        .collect(Collectors.toMap(
            InformationSource::name,
            InformationSource::getDisplayName,
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
