package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Collections;
import java.util.List;

public record GeneralDetailSummaryView(String prospectName,
                                       String onshoreOffshore,
                                       String prospectLocatedInUkcs,
                                       String percentageLocatedInUkcs,
                                       String informationSource,
                                       String organisationNameAndNumber,
                                       Boolean isLicenceApplicationInfoSource,
                                       String licenceApplication,
                                       List<String> quadAndBlockDisplayList) {
  public static GeneralDetailSummaryView empty() {
    return new GeneralDetailSummaryView(
        "",
        "",
        "",
        "",
        "",
        "",
        false,
        "",
        Collections.emptyList());
  }
}
