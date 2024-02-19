package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class FieldMinimumMostLikelyMaxBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {

  @Override
  public boolean isMinAvailable() {
    return true;
  }

  @Override
  public boolean isMaxAvailable() {
    return true;
  }

  @Override
  public boolean isMostLikelyAvailable() {
    return true;
  }

  @Override
  public boolean isMostLikelyRequired() {
    return true;
  }

  @Override
  public boolean allowEquivalentValues() {
    return true;
  }

  @Override
  public ProbabilityDistributionValidationError getMinMaxError() {
    return ProbabilityDistributionValidationError.MIN_MAX_ALLOW_EQUIVALENT;
  }
}
