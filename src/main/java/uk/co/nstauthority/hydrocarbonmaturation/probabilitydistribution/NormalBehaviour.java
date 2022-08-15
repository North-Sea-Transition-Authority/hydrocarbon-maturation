package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class NormalBehaviour implements TypeBehaviour {

  @Override
  public boolean isMinAvailable() {
    return true;
  }

  @Override
  public boolean isMinRequired() {
    return false;
  }

  @Override
  public boolean isP99Available() {
    return true;
  }

  @Override
  public boolean isP99Required() {
    return false;
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
    return false;
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
  public boolean isConstantAvailable() {
    return false;
  }

  @Override
  public boolean isConstantRequired() {
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

  @Override
  public boolean isP1Available() {
    return true;
  }

  @Override
  public boolean isP1Required() {
    return false;
  }

  @Override
  public boolean isMaxAvailable() {
    return true;
  }

  @Override
  public boolean isMaxRequired() {
    return false;
  }
}
