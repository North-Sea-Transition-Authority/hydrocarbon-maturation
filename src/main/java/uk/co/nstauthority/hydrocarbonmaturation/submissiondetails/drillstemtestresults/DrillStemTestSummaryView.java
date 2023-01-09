package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.List;

public record DrillStemTestSummaryView(
    String hasMoreDrillStemTestResults,
    List<DrillStemTestResultSummaryView> drillStemTestResultSummaryViews) {
  public static DrillStemTestSummaryView empty() {
    return new DrillStemTestSummaryView("", List.of());
  }
}
