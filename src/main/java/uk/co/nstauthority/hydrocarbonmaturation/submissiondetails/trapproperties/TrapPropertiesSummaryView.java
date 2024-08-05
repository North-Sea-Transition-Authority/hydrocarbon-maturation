package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

public record TrapPropertiesSummaryView(String trapDescription,
                                        String trapType,
                                        String otherTrapType,
                                        String latitudeDegrees,
                                        String latitudeMinutes,
                                        String latitudeSeconds,
                                        String longitudeDegrees,
                                        String longitudeMinutes,
                                        String longitudeSeconds,
                                        String longitudeHemisphere,
                                        String datum,
                                        String quadAndBlockCentreOfInterest,
                                        ProbabilityDistributionTableView tableView,
                                        List<SummaryFileViewRow> prospectDepthMap) {
  public static TrapPropertiesSummaryView empty() {
    return new TrapPropertiesSummaryView("", "", "", "",
        "", "", "", "",
        "", "", "", "",
        new ProbabilityDistributionTableView(new SummaryBehaviour(), List.of()),
        List.of(SummaryFileViewRow.empty("")));
  }
}
