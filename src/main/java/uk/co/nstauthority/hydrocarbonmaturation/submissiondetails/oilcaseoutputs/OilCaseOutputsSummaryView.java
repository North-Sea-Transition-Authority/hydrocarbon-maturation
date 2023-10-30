package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record OilCaseOutputsSummaryView(String hasAssociatedGasRecoverableResources,
                                        List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
  public static OilCaseOutputsSummaryView empty() {
    return new OilCaseOutputsSummaryView(
        "",
        List.of(
            ProbabilityDistributionSummaryView.empty("Oil in place"),
            ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Risked oil equivalent recoverable")
        )
    );
  }
}
