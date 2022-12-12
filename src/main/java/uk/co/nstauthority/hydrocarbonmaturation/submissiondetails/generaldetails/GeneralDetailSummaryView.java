package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public record GeneralDetailSummaryView(SubmissionType submissionType,
                                       String resourceName,
                                       String onshoreOffshore,
                                       String isResourceOnALicence,
                                       Boolean showLicences,
                                       String resourceLocatedInUkcs,
                                       String percentageLocatedInUkcs,
                                       String informationSource,
                                       String organisationNameAndNumber,
                                       Boolean isLicenceApplicationInfoSource,
                                       String licenceApplication,
                                       List<String> quadAndBlockDisplayList,
                                       List<String> licenceDisplayList,
                                       String resourceType) {
  public static GeneralDetailSummaryView empty(SubmissionType submissionType,
                                               String resourceType) {
    return new GeneralDetailSummaryView(
        submissionType,
        "",
        "",
        "",
        Boolean.TRUE,
        "",
        "",
        "",
        "",
        false,
        "",
        Collections.emptyList(),
        Collections.emptyList(),
        resourceType);
  }
}
