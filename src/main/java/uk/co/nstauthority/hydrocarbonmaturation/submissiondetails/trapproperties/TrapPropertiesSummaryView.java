package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.List;
import java.util.Map;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;

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
                                        Map<UploadedFile, String> prospectDepthMap) {
  public static TrapPropertiesSummaryView empty() {
    return new TrapPropertiesSummaryView("", "", "", "",
        "", "", "", "",
        "", "", "", "",
        new ProbabilityDistributionTableView(new SummaryBehaviour(), List.of()), Map.of());
  }
}
