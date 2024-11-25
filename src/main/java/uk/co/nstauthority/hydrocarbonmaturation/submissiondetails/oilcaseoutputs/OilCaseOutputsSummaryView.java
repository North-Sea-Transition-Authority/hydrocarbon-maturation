package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;

public record OilCaseOutputsSummaryView(String hasAssociatedGasRecoverableResources,
                                        String useIndustryStandardConversion,
                                        ProbabilityDistributionTableView tableView) {
  public static OilCaseOutputsSummaryView empty(SummaryBehaviour summaryBehaviour) {
    var emptyTableView = new ProbabilityDistributionTableView(
        summaryBehaviour,
        List.of(
            ProbabilityDistributionSummaryView.empty("Oil initially in place"),
            ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Risked oil equivalent recoverable")
        )
    );
    return new OilCaseOutputsSummaryView("", "", emptyTableView);
  }
}
