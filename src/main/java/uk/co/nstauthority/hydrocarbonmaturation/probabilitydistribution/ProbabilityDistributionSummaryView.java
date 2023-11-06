package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public record ProbabilityDistributionSummaryView(
    String probabilityDistributionName,
    String probabilityDistributionType,
    String min,
    String p99,
    String p90,
    String oneC,
    String mode,
    String p50,
    String twoC,
    String meanOrConstant,
    String p10,
    String threeC,
    String p1,
    String mostLikely,
    String max,
    String units,
    String oneP,
    String twoP,
    String threeP,
    String comments
) {
  public static ProbabilityDistributionSummaryView empty(String probabilityDistributionName) {
    return new ProbabilityDistributionSummaryView(
        probabilityDistributionName,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "");
  }
}
