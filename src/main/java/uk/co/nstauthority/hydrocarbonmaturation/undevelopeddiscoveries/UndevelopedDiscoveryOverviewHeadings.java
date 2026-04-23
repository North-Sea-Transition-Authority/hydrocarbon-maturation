package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum UndevelopedDiscoveryOverviewHeadings implements Displayable {
  DISCOVERY(0, "Discovery"),
  YEAR_ADDED(1, "Year added"),
  LICENSED(2, "Licensed"),
  GROUP_LICENSED(3, "Group licensed"),
  GEOGRAPHICAL_AREA(4, "Geographical area"),
  OIL_EQUIVALENT_1C_HEADING(5, "mmboe (1C)"),
  OIL_EQUIVALENT_2C_HEADING(6, "mmboe (2C)"),
  OIL_EQUIVALENT_3C_HEADING(7, "mmboe (3C)"),
  PERCENTAGE_GAS(8, "Percentage gas"),
  COMPLEXITY(9, "Complexity");

  private final int columnIndex;
  private final String displayName;

  UndevelopedDiscoveryOverviewHeadings(int columnIndex, String displayName) {
    this.columnIndex = columnIndex;
    this.displayName = displayName;
  }

  public static List<String> getDisplayNames() {
    return Arrays.stream(UndevelopedDiscoveryOverviewHeadings.values())
        .sorted(Comparator.comparingInt(UndevelopedDiscoveryOverviewHeadings::getColumnIndex))
        .map(UndevelopedDiscoveryOverviewHeadings::getDisplayName)
        .toList();
  }

  public int getColumnIndex() {
    return columnIndex;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
