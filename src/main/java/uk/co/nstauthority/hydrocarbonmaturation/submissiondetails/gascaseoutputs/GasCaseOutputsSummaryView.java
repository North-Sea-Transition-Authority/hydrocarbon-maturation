package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;

public record GasCaseOutputsSummaryView(String hasAssociatedCondensateRecoverableResources,
                                        ProbabilityDistributionTableView tableView) {
  public static GasCaseOutputsSummaryView empty(SummaryBehaviour summaryBehaviour) {
    var emptyTableView = new ProbabilityDistributionTableView(
        summaryBehaviour,
        List.of(
            ProbabilityDistributionSummaryView.empty("Gas initially in place"),
            ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
        )
    );

    return new GasCaseOutputsSummaryView("", emptyTableView);
  }
}
