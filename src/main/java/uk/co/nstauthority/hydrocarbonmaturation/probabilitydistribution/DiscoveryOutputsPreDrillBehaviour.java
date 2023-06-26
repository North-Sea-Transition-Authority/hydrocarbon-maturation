package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class DiscoveryOutputsPreDrillBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {

  @Override
  public boolean isP90Available() {
    return true;
  }

  @Override
  public boolean isP90Required() {
    return true;
  }

  @Override
  public boolean isP50Available() {
    return true;
  }

  @Override
  public boolean isP50Required() {
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
  public boolean isP10Available() {
    return true;
  }

  @Override
  public boolean isP10Required() {
    return true;
  }
}
