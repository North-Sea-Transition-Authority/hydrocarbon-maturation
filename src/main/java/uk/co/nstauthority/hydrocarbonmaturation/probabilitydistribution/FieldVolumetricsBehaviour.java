package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class FieldVolumetricsBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {
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
}
