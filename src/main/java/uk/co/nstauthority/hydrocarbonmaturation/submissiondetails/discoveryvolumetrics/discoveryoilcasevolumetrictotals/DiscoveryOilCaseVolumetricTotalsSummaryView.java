package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record DiscoveryOilCaseVolumetricTotalsSummaryView(
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {

  public static DiscoveryOilCaseVolumetricTotalsSummaryView empty() {
    return new DiscoveryOilCaseVolumetricTotalsSummaryView(
        List.of(
            ProbabilityDistributionSummaryView.empty("Oil initially in place"),
            ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")));
  }
}
