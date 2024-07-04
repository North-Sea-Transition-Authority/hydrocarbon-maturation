package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public record ReservoirPropertiesSummaryView(SubmissionType submissionType,
                                             String formationName,
                                             String subUnit,
                                             String ageStageOrSeries,
                                             String groupName,
                                             String lithology,
                                             String reservoirFacies,
                                             String reservoirConnectivity,
                                             ProbabilityDistributionTableView tableView,
                                             ProbabilityDistributionTableView depthToContactDistributions) {
}
