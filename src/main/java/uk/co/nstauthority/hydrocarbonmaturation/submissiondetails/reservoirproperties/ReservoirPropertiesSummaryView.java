package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record ReservoirPropertiesSummaryView(String formationName,
                                             String ageStageOrSeries,
                                             String lithology,
                                             List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
}
