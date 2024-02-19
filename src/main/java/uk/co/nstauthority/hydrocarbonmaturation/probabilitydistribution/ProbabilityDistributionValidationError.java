package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

enum ProbabilityDistributionValidationError {

  GENERAL_ERROR("%s - all mandatory probability of exceedence inputs must be provided and " +
      "all inputs must be numbers with up to 2 decimal places"),
  PERCENTAGE_ERROR("%s - all mandatory probability of exceedence inputs must be provided and " +
      "all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%%"),
  MIN_MAX("%s - min value must be less than max value"),
  MIN_MAX_ALLOW_EQUIVALENT("%s - min value must be less than or equal to max value"),
  P99_TO_P1("%s - P99 to P1 values must be in the order of P99 < P90 < P50 < P10 < P1"),
  P90_TO_P10("%s - P90 to P10 values must be in the order of P90 < P50 < P10"),
  P90_TO_P10_ALLOW_EQUIVALENT("%s - P90 to P10 values must be in the order of P90 ≤ P50 ≤ P10"),
  ONE_C_TO_THREE_C("%s - 1C to 3C values must be in the order of 1C ≤ 2C ≤ 3C"),
  ONE_P_TO_THREE_P("%s - 1P to 3P values must be in the order of 1P ≤ 2P ≤ 3P");

  private final String errorMessage;

  ProbabilityDistributionValidationError(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
