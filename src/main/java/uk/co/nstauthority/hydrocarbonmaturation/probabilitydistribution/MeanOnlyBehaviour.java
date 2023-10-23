package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class MeanOnlyBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {

  @Override
  public boolean isMeanAvailable() {
    return true;
  }

  @Override
  public boolean isMeanRequired() {
    return true;
  }
}
