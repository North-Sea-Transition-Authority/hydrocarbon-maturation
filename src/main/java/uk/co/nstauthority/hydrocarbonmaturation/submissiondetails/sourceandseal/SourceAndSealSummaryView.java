package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

public record SourceAndSealSummaryView(String sourceFormationName,
                                       String sourceAgeStage,
                                       String sourceGroupName,
                                       String sourceLithology,
                                       String sealFormationName,
                                       String sealAgeStage,
                                       String sealGroupName,
                                       String sealLithology) {

  public static SourceAndSealSummaryView empty() {
    return new SourceAndSealSummaryView("", "", "", "", "", "", "", "");
  }
}
