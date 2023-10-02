package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

public record DiscoveryWellDetailsSummaryView(
    String principalWell,
    String latitudeDegrees,
    String latitudeMinutes,
    String latitudeSeconds,
    String longitudeDegrees,
    String longitudeMinutes,
    String longitudeSeconds,
    String longitudeHemisphere,
    String datum,
    String totalDepthFormation,
    String totalDepthFormationComments,
    String dataAcquisitionProgramme,
    String geophysicalUpdate,
    String discoveryStatus,
    String commentsRegardingCommerciality,
    String totalWellCost,
    String successRelatedCosts) {

  public static DiscoveryWellDetailsSummaryView empty() {
    return new DiscoveryWellDetailsSummaryView(
        "", "", "",
        "", "", "", "",
        "", "", "", "",
        "", "", "", "",
        "", "");
  }
}