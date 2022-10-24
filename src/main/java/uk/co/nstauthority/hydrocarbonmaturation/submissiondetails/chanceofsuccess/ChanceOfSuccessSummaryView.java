package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

public record ChanceOfSuccessSummaryView(String playSourcePercentage,
                                         String playSourceComments,
                                         String playReservoirPercentage,
                                         String playReservoirComments,
                                         String playTopSealPercentage,
                                         String playTopSealComments,
                                         String totalPlayChanceOfSucces,
                                         String prospectSpecificSourcePercentage,
                                         String prospectSpecificSourceComments,
                                         String prospectSpecificReservoirPercentage,
                                         String prospectSpecificReservoirComments,
                                         String prospectSpecificTopSealPercentage,
                                         String prospectSpecificTopSealComments,
                                         String prospectSpecificTrapPercentage,
                                         String prospectSpecificTrapComments,
                                         String prospectSpecificMigrationPercentage,
                                         String prospectSpecificMigrationComments,
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
        "", "", "", "",
        "", "", "", "",
        "", "", "", "",
        "", "", "", "", "",
        "", "", "", "");
  }
}
