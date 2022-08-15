package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public enum ProbabilityDistributionType {
  NORMAL(NormalBehaviour.class),
  CUSTOM;

  private Class<? extends TypeBehaviour> presetBehaviour;
  private final boolean hasPresetBehaviour;

  ProbabilityDistributionType(Class<? extends TypeBehaviour> presetBehaviour) {
    this.presetBehaviour = presetBehaviour;
    this.hasPresetBehaviour = true;
  }

  ProbabilityDistributionType() {
    this.hasPresetBehaviour = false;
  }

  public Class<? extends TypeBehaviour> getPresetBehaviour() {
    return presetBehaviour;
  }

  public boolean hasPresetBehaviour() {
    return hasPresetBehaviour;
  }
}
