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
                                            String kvKhMin,
                                            String kvKhMax,
                                            String kvKhAverage,
                                            String kvKhAverageMethodUsed,
                                            String kvKhSource,
                                            String otherKvKhSource,
                                            String kvKhComments) {

  public static PermeabilityDetailSummaryView empty() {
    return new PermeabilityDetailSummaryView(
        "", true, "", "",
         "", "", "",
        "", "", "", "",
        "", "", "", "", "");
  }
}
