package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;

public record GeneralDetailSummaryView(String resourceName,
                                       String onshoreOffshore,
                                       String resourceLocatedInUkcs,
                                       String percentageLocatedInUkcs,
                                       String informationSource,
                                       String organisationNameAndNumber,
                                       Boolean isLicenceApplicationInfoSource,
                                       String licenceApplication,
                                       List<String> quadAndBlockDisplayList,
                                       String resourceType) {
  public static GeneralDetailSummaryView empty(String resourceType) {
    return new GeneralDetailSummaryView(
        "",
        "",
        "",
        "",
        "",
        "",
        false,
        "",
        Collections.emptyList(),
        resourceType);
  }
}
