package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

public record GeneralDetailSummaryView(String prospectName,
                                       String onshoreOffshore,
                                       String prospectLocatedInUkcs,
                                       String percentageLocatedInUkcs,
                                       String informationSource,
                                       Boolean isLicenceApplicationInfoSource,
                                       String licenceApplication) {
  public static GeneralDetailSummaryView empty() {
    return new GeneralDetailSummaryView(
        "",
        "",
        "",
        "",
        "",
        false,
        "");
  }
}
