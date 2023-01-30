package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

public record DrillStemTestResultSummaryView(String testNumber,
                                             String formationName,
                                             String topDepth,
                                             String bottomDepth,
                                             String bhfp,
                                             String whfp,
                                             String flowDuration,
                                             String gasFlowrate,
                                             String oilFlowrate,
                                             String waterFlowrate,
                                             String commentsAndInterpretation,
                                             DrillStemTestResultSummaryMode summaryMode,
                                             String changeUrl,
                                             String deleteUrl) {
  public static DrillStemTestResultSummaryView empty(Integer resultNumber, String changeUrl, String deleteUrl) {
    return new DrillStemTestResultSummaryView(resultNumber.toString(), "", "",
        "", "", "", "", "", "", "", "", DrillStemTestResultSummaryMode.EDIT, changeUrl, deleteUrl);
  }
}
