package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public record ProbabilityDistributionTypeError(
    boolean hasError,
    String fullErrorPath,
    ProbabilityDistributionType probabilityDistributionType,
    String errorId
) {

  public static ProbabilityDistributionTypeError emptyError() {
    return new ProbabilityDistributionTypeError(false, null, null, null);
  }

}
