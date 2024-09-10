package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum UndevelopedDiscoveryOverviewHeadings {
  DISCOVERY(0, "Discovery"),
  YEAR_ADDED(1, "Year added"),
  LICENSED(2, "Licensed"),
  GEOGRAPHICAL_AREA(3, "Geographical area"),
  OIL_EQUIVALENT_1C_HEADING(4, "mmboe (1C)"),
  OIL_EQUIVALENT_2C_HEADING(5, "mmboe (2C)"),
  OIL_EQUIVALENT_3C_HEADING(6, "mmboe (3C)"),
  PERCENTAGE_GAS(7, "Percentage gas"),
  COMPLEXITY(8, "Complexity");

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

  public String getDisplayName() {
    return displayName;
  }
}
