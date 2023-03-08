package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;

public record OpportunityDetailsSummaryView(String opportunityType,
                                            Boolean otherOpportunity,
                                            String otherOpportunityType,
                                            String opportunityComments,
                                            String keyEvaluationWork,
                                            String keyWorkRequired,
                                            Map<UploadedFile, String> mapOfOpportunity,
                                            Map<UploadedFile, String> geologicalCrossSection) {

  public static OpportunityDetailsSummaryView empty() {
    return new OpportunityDetailsSummaryView(
        "",
        false,
        "",
        "",
        "",
        "",
        Map.of(),
        Map.of());
  }
}
