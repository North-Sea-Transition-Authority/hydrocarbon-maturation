package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;

public record ProbabilityDistributionTableView(SummaryBehaviour summaryBehaviour,
                                               List<ProbabilityDistributionSummaryView> probabilityDistributions) {
  public static ProbabilityDistributionTableView empty() {
    return new ProbabilityDistributionTableView(new SummaryBehaviour(), List.of());
  }
}
