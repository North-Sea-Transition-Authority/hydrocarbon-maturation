package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

public record PermeabilityDetailSummaryView(String hasPermeability,
                                            Boolean hasPermeabilityBol,
                                            String permeabilityMin,
                                            String permeabilityMax,
                                            String permeabilityAverage,
                                            String averageMethodUsed,
                                            String permeabilitySource,
                                            String otherPermeabilitySource,
                                            String permeabilityComments,
                                            String kvKhAverage) {

  public static PermeabilityDetailSummaryView empty() {
    return new PermeabilityDetailSummaryView(
        "", true, "", "",
         "", "", "",
        "", "", "");
  }
}
