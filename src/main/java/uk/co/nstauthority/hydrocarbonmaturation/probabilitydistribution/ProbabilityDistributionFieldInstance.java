package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.NORMAL;

import java.util.Set;

public enum ProbabilityDistributionFieldInstance {
  DEPTH_TO_CREST_PROSPECT_DISCOVERY(Set.of(NORMAL)),

  //TODO remove example custom enum value
  DEPTH_TO_CREST_FIELD_SUBMISSION(CustomBehaviour.class),
  ;
  private Class<? extends TypeBehaviour> customBehaviour;
  private Set<ProbabilityDistributionType> presetTypes;

  private final Type type;

  private enum Type {
    PRESET_LIST, CUSTOM
  }

  ProbabilityDistributionFieldInstance(Set<ProbabilityDistributionType> presetTypes) {
    this.presetTypes = presetTypes;

    type = Type.PRESET_LIST;
  }

  ProbabilityDistributionFieldInstance(Class<? extends TypeBehaviour> customBehaviour) {
    this.customBehaviour = customBehaviour;

    type = Type.CUSTOM;
  }

  public Class<? extends TypeBehaviour> getCustomBehaviour() {
    return customBehaviour;
  }

  public Set<ProbabilityDistributionType> getPresetTypes() {
    return presetTypes;
  }

  public Type getType() {
    return type;
  }
}
