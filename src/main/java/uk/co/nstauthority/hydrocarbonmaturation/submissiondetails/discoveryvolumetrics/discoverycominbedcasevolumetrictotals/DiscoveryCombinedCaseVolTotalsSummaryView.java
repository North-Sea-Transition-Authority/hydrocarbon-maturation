package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record DiscoveryCombinedCaseVolTotalsSummaryView(
    String hasAssociatedCondensateRecoverableResources,
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
  public static DiscoveryCombinedCaseVolTotalsSummaryView empty() {
    return new DiscoveryCombinedCaseVolTotalsSummaryView(
        "",
        List.of(
            ProbabilityDistributionSummaryView.empty("Oil initially in place"),
            ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Gas initially in place"),
            ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
        )
    );
  }
}
