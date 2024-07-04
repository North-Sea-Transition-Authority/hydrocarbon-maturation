package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;

public record MixedCaseOutputsSummaryView(String hasAssociatedGasRecoverableResources,
                                          String hasAssociatedCondensateRecoverableResources,
                                          ProbabilityDistributionTableView tableView) {

  public static MixedCaseOutputsSummaryView empty(SummaryBehaviour summaryBehaviour) {
    var emptyTableView = new ProbabilityDistributionTableView(
        summaryBehaviour,
        List.of(
            ProbabilityDistributionSummaryView.empty("Oil initially in place"),
            ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Risked oil equivalent recoverable"),
            ProbabilityDistributionSummaryView.empty("Gas initially in place"),
            ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
        )
    );
    return new MixedCaseOutputsSummaryView("", "", emptyTableView);
  }
}
