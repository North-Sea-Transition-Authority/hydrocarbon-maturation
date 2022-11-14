package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

public record DryWellDetailsSummaryView(String principalWell,
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
                                        String dryHoleType,
                                        String dryHoleDescription,
                                        String dryHoleScope,
                                        String dryHoleComments,
                                        String dryHoleCost) {

  public static DryWellDetailsSummaryView empty() {
    return new DryWellDetailsSummaryView(
        "", "", "", "",
        "", "", "", "",
        "", "", "", "",
        "", "", "", "",
        "", "");
  }
}
