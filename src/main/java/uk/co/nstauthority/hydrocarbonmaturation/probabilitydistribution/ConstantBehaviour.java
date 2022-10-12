package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class ConstantBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {
  @Override
  public boolean isConstantAvailable() {
    return true;
  }

  @Override
  public boolean isConstantRequired() {
    return true;
  }
}
