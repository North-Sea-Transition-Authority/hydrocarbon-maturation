package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record DiscoveryGasCaseVolumetricTotalsSummaryView(
    String hasAssociatedCondensateRecoverableResources,
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
  public static DiscoveryGasCaseVolumetricTotalsSummaryView empty() {
    return new DiscoveryGasCaseVolumetricTotalsSummaryView(
        "",
        List.of(
            ProbabilityDistributionSummaryView.empty("Gas in place"),
            ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
        )
    );
  }
}
