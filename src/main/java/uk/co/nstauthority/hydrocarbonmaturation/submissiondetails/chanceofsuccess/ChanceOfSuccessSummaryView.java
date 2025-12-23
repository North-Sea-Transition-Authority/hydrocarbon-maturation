package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

public record ChanceOfSuccessSummaryView(String playSourcePercentage,
                                         String playSourceComments,
                                         String playReservoirPercentage,
                                         String playReservoirComments,
                                         String playTopSealPercentage,
                                         String playTopSealComments,
                                         String totalPlayChanceOfSuccess,
                                         String prospectSpecificTrapPresence,
                                         String prospectSpecificTrapPresenceComments,
                                         String prospectSpecificTrapEffectiveness,
                                         String prospectSpecificTrapEffectivenessComments,
                                         String prospectSpecificSourcePresence,
                                         String prospectSpecificSourcePresenceComments,
                                         String prospectSpecificSourceEffectiveness,
                                         String prospectSpecificSourceEffectivenessComments,
                                         String prospectSpecificReservoirPresence,
                                         String prospectSpecificReservoirPresenceComments,
                                         String prospectSpecificReservoirEffectiveness,
                                         String prospectSpecificReservoirEffectivenessComments,
                                         String totalProspectSpecificChance,
                                         String overallChanceOfSuccess,
                                         String geophysicallyModified,
                                         String geophysicallyModifiedPercentage,
                                         String geophysicallyModifiedComments,
                                         String specificWellLocation,
                                         String wellModifiedPercentage,
                                         String wellModifiedComments) {

  public static ChanceOfSuccessSummaryView empty() {
    return new ChanceOfSuccessSummaryView("", "", "", "",
        "", "", "", "", "", "",
        "", "", "", "",
        "", "", "", "",
        "", "", "", "", "",
        "", "", "", "");
  }
}
