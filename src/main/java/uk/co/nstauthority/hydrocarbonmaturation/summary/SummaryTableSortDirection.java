package uk.co.nstauthority.hydrocarbonmaturation.summary;

public enum SummaryTableSortDirection {
  ASCENDING(1),
  DESCENDING(-1),
  NONE(0);

  private final int frontendSortValue;

  SummaryTableSortDirection(int frontendSortValue) {
    this.frontendSortValue = frontendSortValue;
  }

  public int getFrontendSortValue() {
    return frontendSortValue;
  }
}
