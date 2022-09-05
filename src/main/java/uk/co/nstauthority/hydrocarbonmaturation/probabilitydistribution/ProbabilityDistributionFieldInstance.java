package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.BETA;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.CONSTANT;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.LOGNORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.NORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.RECTANGULAR;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.TRIANGULAR;

import java.util.List;

public enum ProbabilityDistributionFieldInstance {
  DEPTH_TO_CREST_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      "ft tvdss"
  ),
  DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      "ft tvdss"
  ),
  PRODUCTIVE_AREA(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      "km2"
  ),

  //TODO remove example custom enum value
  DEPTH_TO_CREST_FIELD_SUBMISSION(CustomBehaviour.class),
  ;
  private Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour;
  private List<ProbabilityDistributionType> presetTypes;

  private String units;

  private final Type type;

  private enum Type {
    PRESET_LIST, CUSTOM
  }

  ProbabilityDistributionFieldInstance(
      List<ProbabilityDistributionType> presetTypes,
      String units) {
    this.presetTypes = presetTypes;
    this.units = units;

    type = Type.PRESET_LIST;
  }

  ProbabilityDistributionFieldInstance(Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour) {
    this.customBehaviour = customBehaviour;

    type = Type.CUSTOM;
  }

  public Class<? extends ProbabilityDistributionTypeBehaviour> getCustomBehaviour() {
    return customBehaviour;
  }

  public List<ProbabilityDistributionType> getPresetTypes() {
    return presetTypes;
  }

  public Type getType() {
    return type;
  }

  public String getUnits() {
    return units;
  }
}
