package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class RectangularBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {

  @Override
  public boolean isMinAvailable() {
    return true;
  }

  @Override
  public boolean isMinRequired() {
    return true;
  }

  @Override
  public boolean isMeanAvailable() {
    return true;
  }

  @Override
  public boolean isMeanRequired() {
    return false;
  }

  @Override
  public boolean isMaxAvailable() {
    return true;
  }

  @Override
  public boolean isMaxRequired() {
    return true;
  }

  @Override
  public ProbabilityDistributionValidationError getMinMaxError() {
    return ProbabilityDistributionValidationError.MIN_MAX;
  }
}
