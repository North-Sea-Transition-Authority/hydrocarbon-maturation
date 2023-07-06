package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public record GeneralDetailSummaryView(SubmissionType submissionType,
                                       String resourceLocatedInUkcs,
                                       String resourceLocatedInEngland,
                                       String percentageLocatedInUkcs,
                                       String percentageLocatedInEngland,
                                       String informationSource,
                                       Boolean isLicenceApplicationInfoSource,
                                       String licenceApplication,
                                       List<String> quadAndBlockDisplayList,
                                       List<String> licenceDisplayList,
                                       String resourceType,
                                       String relinquishmentDate) {
  public static GeneralDetailSummaryView empty(SubmissionType submissionType,
                                               String resourceType) {
    return new GeneralDetailSummaryView(
        submissionType,
        "",
        "",
        "",
        "",
        "",
        false,
        "",
        Collections.emptyList(),
        Collections.emptyList(),
        resourceType,
        "");
  }
}
