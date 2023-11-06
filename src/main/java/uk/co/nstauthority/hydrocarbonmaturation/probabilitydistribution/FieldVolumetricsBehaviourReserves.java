package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class FieldVolumetricsBehaviourReserves extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {
  @Override
  public boolean is1PAvailable() {
    return true;
  }

  @Override
  public boolean is1PRequired() {
    return true;
  }

  @Override
  public boolean is2PAvailable() {
    return true;
  }

  @Override
  public boolean is2PRequired() {
    return true;
  }

  @Override
  public boolean is3PAvailable() {
    return true;
  }

  @Override
  public boolean is3PRequired() {
    return true;
  }
}
