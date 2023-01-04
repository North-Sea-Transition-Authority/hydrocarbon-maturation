package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class MinimumMostLikelyMaxBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {

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
}
