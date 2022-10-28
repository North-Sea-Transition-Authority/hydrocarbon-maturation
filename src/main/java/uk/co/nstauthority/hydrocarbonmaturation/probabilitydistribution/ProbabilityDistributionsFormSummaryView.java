package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;

//summary view for forms with only probability distribution questions.
public record ProbabilityDistributionsFormSummaryView(
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
}
