package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class FieldVolumetricsResourcesBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {
  @Override
  public boolean is1CAvailable() {
    return true;
  }

  @Override
  public boolean is1CRequired() {
    return true;
  }

  @Override
  public boolean is2CAvailable() {
    return true;
  }

  @Override
  public boolean is2CRequired() {
    return true;
  }

  @Override
  public boolean is3CAvailable() {
    return true;
  }

  @Override
  public boolean is3CRequired() {
    return true;
  }

  @Override
  public boolean allowEquivalentValues() {
    return true;
  }

  @Override
  public ProbabilityDistributionValidationError get1CTo3CError() {
    return ProbabilityDistributionValidationError.ONE_C_TO_THREE_C;
  }
}
