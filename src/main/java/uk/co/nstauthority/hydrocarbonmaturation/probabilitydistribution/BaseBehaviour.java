package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;


public abstract class BaseBehaviour implements ProbabilityDistributionTypeBehaviour {

  @Override
  public boolean isMinAvailable() {
    return false;
  }

  @Override
  public boolean isMinRequired() {
    return false;
  }

  @Override
  public boolean isP99Available() {
    return false;
  }

  @Override
  public boolean isP99Required() {
    return false;
  }

  @Override
  public boolean isP90Available() {
    return false;
  }

  @Override
  public boolean isP90Required() {
    return false;
  }

  @Override
  public boolean isModeAvailable() {
    return false;
  }

  @Override
  public boolean isModeRequired() {
    return false;
  }

  @Override
  public boolean isP50Available() {
    return false;
  }

  @Override
  public boolean isP50Required() {
    return false;
  }

  @Override
  public boolean isMeanAvailable() {
    return false;
  }

  @Override
  public boolean isMeanRequired() {
    return false;
  }

  @Override
  public boolean isConstantAvailable() {
    return false;
  }

  @Override
  public boolean isConstantRequired() {
    return false;
  }

  @Override
  public boolean isP10Available() {
    return false;
  }

  @Override
  public boolean isP10Required() {
    return false;
  }

  @Override
  public boolean isP1Available() {
    return false;
  }

  @Override
  public boolean isP1Required() {
    return false;
  }

  @Override
  public boolean isMaxAvailable() {
    return false;
  }

  @Override
  public boolean isMaxRequired() {
    return false;
  }
}
