package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public record ProbabilityDistributionSummaryView(String probabilityDistributionName,
                                                 String probabilityDistributionType,
                                                 String min,
                                                 String p99,
                                                 String p90,
                                                 String mode,
                                                 String p50,
                                                 String meanOrConstant,
                                                 String p10,
                                                 String p1,
                                                 String max,
                                                 String units) {
  public static ProbabilityDistributionSummaryView empty(String probabilityDistributionName) {
    return new ProbabilityDistributionSummaryView(probabilityDistributionName, "", "", "", "", "", "", "", "", "", "", "");
  }
}
