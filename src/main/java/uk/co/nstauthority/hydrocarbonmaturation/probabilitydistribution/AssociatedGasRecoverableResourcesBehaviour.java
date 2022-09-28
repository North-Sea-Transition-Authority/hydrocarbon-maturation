package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class AssociatedGasRecoverableResourcesBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {
  @Override
  public boolean isP99Available() {
    return true;
  }

  @Override
  public boolean isP99Required() {
    return true;
  }

  @Override
  public boolean isP90Available() {
    return true;
  }

  @Override
  public boolean isP90Required() {
    return true;
  }

  @Override
  public boolean isModeAvailable() {
    return true;
  }

  @Override
  public boolean isModeRequired() {
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
    return true;
  }

  @Override
  public boolean isP10Available() {
    return true;
  }

  @Override
  public boolean isP10Required() {
    return true;
  }

  @Override
  public boolean isP1Available() {
    return true;
  }

  @Override
  public boolean isP1Required() {
    return true;
  }
}
