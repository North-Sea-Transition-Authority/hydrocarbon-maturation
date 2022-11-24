package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.List;
import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

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
                                        List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews,
                                        Map<UploadedFile, String> prospectDepthMap) {
}
