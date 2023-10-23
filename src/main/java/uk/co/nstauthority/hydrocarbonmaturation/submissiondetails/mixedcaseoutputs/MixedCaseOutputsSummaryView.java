package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record MixedCaseOutputsSummaryView(String hasAssociatedGasRecoverableResources,
                                          List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {

  public static MixedCaseOutputsSummaryView empty() {
    return new MixedCaseOutputsSummaryView(
        "",
        List.of(
            ProbabilityDistributionSummaryView.empty("Oil in place"),
            ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Risked oil equivalent recoverable"),
            ProbabilityDistributionSummaryView.empty("Gas in place"),
            ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
            ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")));
  }
}
