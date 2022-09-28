package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.BETA;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.CONSTANT;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.CUSTOM;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.LOGNORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.NORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.RECTANGULAR;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.TRIANGULAR;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.util.ReflectionUtils;

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
  OIL_CASE_OUTPUTS_OIL_IN_PLACE(OilInPlaceBehaviour.class, "mmstb"),
  ;
  private Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour;
  private final List<ProbabilityDistributionType> presetTypes;

  private final String units;

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

  ProbabilityDistributionFieldInstance(Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour,
                                       String units) {
    this.customBehaviour = customBehaviour;
    this.units = units;

    this.presetTypes = List.of(CUSTOM);
    type = Type.CUSTOM;
  }

  public Class<? extends ProbabilityDistributionTypeBehaviour> getCustomBehaviour() {
    return customBehaviour;
  }

  public List<ProbabilityDistributionType> getPresetTypes() {
    return presetTypes;
  }

  public String getUnits() {
    return units;
  }

  public boolean isCustomType() {
    return type == Type.CUSTOM;
  }

  public ProbabilityDistributionTypeBehaviour newInstance() {
    try {
      return ReflectionUtils.accessibleConstructor(this.getCustomBehaviour()).newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new ProbabilityDistributionException(e);
    }
  }
}
