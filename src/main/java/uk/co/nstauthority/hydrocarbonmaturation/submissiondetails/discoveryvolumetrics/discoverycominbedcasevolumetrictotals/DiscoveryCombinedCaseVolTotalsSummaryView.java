package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;

public record DiscoveryCombinedCaseVolTotalsSummaryView(
    String hasAssociatedCondensateRecoverableResources,
    String useIndustryStandardConversion,
    ProbabilityDistributionTableView tableView
) {
  public static DiscoveryCombinedCaseVolTotalsSummaryView empty(SummaryBehaviour summaryBehaviour) {
    var emptyTableView = new ProbabilityDistributionTableView(
        summaryBehaviour,
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

    return new DiscoveryCombinedCaseVolTotalsSummaryView("", "", emptyTableView);
  }
}
