package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public record ReservoirPropertiesSummaryView(SubmissionType submissionType,
                                             String formationName,
                                             String ageStageOrSeries,
                                             String lithology,
                                             String reservoirFacies,
                                             String reservoirConnectivity,
                                             List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
}
