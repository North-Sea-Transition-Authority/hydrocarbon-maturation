package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

public record HydrocarbonCaseSummaryView(String oilOnly,
                                         String oilOnlyProbability,
                                         String oilOnlyComments,
                                         String gasOnly,
                                         String gasOnlyProbability,
                                         String gasOnlyComments,
                                         String oilAndGas,
                                         String oilAndGasProbability,
                                         String oilAndGasComments) {

  public static HydrocarbonCaseSummaryView empty() {
    return new HydrocarbonCaseSummaryView("", "", "", "", "", "", "", "", "");
  }
}