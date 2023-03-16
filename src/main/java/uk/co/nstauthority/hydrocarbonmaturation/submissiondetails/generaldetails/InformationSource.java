package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

public enum InformationSource implements Displayable {
  LICENCE_APPLICATION("Licence application", 10, true),
  WELL_INVESTMENT_DECISION("Well investment decision", 20, true),
  PRE_DRILL_SUBMISSION("Pre-drill submission", 30, true),
  RELINQUISHMENT("Relinquishment", 40, false),
  OTHER("Other", 50, true);

  private final String displayName;
  private final int displayOrder;
  private final boolean isSelectable;

  InformationSource(String displayName, int displayOrder, boolean isSelectable) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.isSelectable = isSelectable;
  }

  public static Map<String, String> getInformationSource() {
    return Arrays.stream(InformationSource.class.getEnumConstants())
        .filter(InformationSource::isSelectable)
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            Displayable::getEnumName,
            Displayable::getDisplayName
        ));
  }

  public static Map<String, String> getDiscoveryInformationSource() {
    return Arrays.stream(InformationSource.values())
        .filter(item -> item == InformationSource.OTHER
            || item == InformationSource.LICENCE_APPLICATION)
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            Displayable::getEnumName,
            Displayable::getDisplayName
        ));
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

  public boolean isSelectable() {
    return isSelectable;
  }
}
