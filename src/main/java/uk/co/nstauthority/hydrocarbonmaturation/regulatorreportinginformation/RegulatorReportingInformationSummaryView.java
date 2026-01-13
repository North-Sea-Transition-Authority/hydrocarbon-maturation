package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

public record RegulatorReportingInformationSummaryView(
    String resourceName,
    String hydrocarbonCase,
    String opportunityType,
    String wellName
) {
  public static RegulatorReportingInformationSummaryView empty() {
    return new RegulatorReportingInformationSummaryView(
        "",
        "",
        "",
        ""
    );
  }
}
