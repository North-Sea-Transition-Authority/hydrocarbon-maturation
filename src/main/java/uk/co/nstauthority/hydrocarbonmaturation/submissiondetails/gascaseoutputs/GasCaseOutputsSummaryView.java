package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record GasCaseOutputsSummaryView(String hasAssociatedCondensateRecoverableResources,
                                        List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
  public static GasCaseOutputsSummaryView empty() {
    return new GasCaseOutputsSummaryView(
        "",
        List.of(
            ProbabilityDistributionSummaryView.empty("Gas initially in place"),
            ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")));
  }
}
