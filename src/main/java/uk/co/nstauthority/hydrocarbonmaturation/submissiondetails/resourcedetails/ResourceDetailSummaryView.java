package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

public record ResourceDetailSummaryView(String resourceName,
                                        String organisationNameAndNumber,
                                        String onshoreOffshore,
                                        String resourceType) {
  public static ResourceDetailSummaryView empty(String resourceType) {
    return new ResourceDetailSummaryView(
        "", "", "", resourceType);
  }
}
