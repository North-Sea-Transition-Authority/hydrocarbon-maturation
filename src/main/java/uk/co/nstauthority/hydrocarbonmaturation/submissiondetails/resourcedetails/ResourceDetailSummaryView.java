package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

public record ResourceDetailSummaryView(String resourceName,
                                        String organisationNameAndNumber,
                                        boolean showOrganisationNameAtTimeOfApproval,
                                        String organisationNameAndNumberAtTimeOfApproval,
                                        String onshoreOffshore,
                                        String resourceType) {
  public static ResourceDetailSummaryView empty(String resourceType) {
    return new ResourceDetailSummaryView(
        "", "", false, "", "", resourceType);
  }

  public boolean isOffshore() {
    return "Offshore".equals(onshoreOffshore);
  }
}
