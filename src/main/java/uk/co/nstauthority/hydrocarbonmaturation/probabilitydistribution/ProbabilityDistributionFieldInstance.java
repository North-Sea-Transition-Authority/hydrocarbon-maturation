package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.BETA;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.CONSTANT;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.CUSTOM;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.LOGNORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.NORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.RECTANGULAR;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.TRIANGULAR;

import java.util.List;

public enum ProbabilityDistributionFieldInstance {
  DEPTH_TO_CREST_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.FT_TVDSS
  ),
  DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.FT_TVDSS
  ),
  AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.M_TVT
  ),
  GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.M_TVT
  ),
  GROSS_ROCK_VOLUME(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.MILLION_M3
  ),
  PERCENT_OF_GRV_OIL_BEARING(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.PERCENT
  ),
  NET_TO_GROSS_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.PERCENT
  ),
  NET_POROSITY_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.KM2
  ),
  DEPTH_TO_HYDROCARBON_CONTACT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.FT_TVDSS
  ),
  GAS_HYDROCARBON_SATURATION(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.PERCENT
  ),
  GAS_EXPANSION_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.SCF_RCF
  ),
  CONDENSATE_GAS_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.STB_MCF
  ),
  GAS_RECOVERY_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.PERCENT
  ),
  OIL_RECOVERY_FACTOR(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.PERCENT
  ),
  FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.RB_STB
  ),
  OIL_HYDROCARBON_SATURATION(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.PERCENT
  ),
  ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.SCF_STB
  ),
  PRODUCTIVE_AREA(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityDistributionUnits.KM2
  ),
  OIL_IN_PLACE(OilInPlaceBehaviour.class, ProbabilityDistributionUnits.MMSTB),
  OIL_RECOVERABLE_RESOURCES(OilRecoverableResourcesBehaviour.class, ProbabilityDistributionUnits.MMSTB),
  ASSOCIATED_GAS_RECOVERABLE_RESOURCES(AssociatedGasRecoverableResourcesBehaviour.class, ProbabilityDistributionUnits.BCF),
  OIL_EQUIVALENT_RECOVERABLE_RESOURCES(OilEquivalentRecoverableResourcesBehaviour.class, ProbabilityDistributionUnits.MMBOE),
  ;
  private Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour;
  private final List<ProbabilityDistributionType> presetTypes;

  private final ProbabilityDistributionUnits units;

  private final Type type;

  private enum Type {
    PRESET_LIST, CUSTOM
  }

  ProbabilityDistributionFieldInstance(
      List<ProbabilityDistributionType> presetTypes,
      ProbabilityDistributionUnits units) {
    this.presetTypes = presetTypes;
    this.units = units;

    type = Type.PRESET_LIST;
  }

  ProbabilityDistributionFieldInstance(Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour,
                                       ProbabilityDistributionUnits units) {
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
    return units.getUnitDisplay();
  }

  public boolean isCustomType() {
    return type == Type.CUSTOM;
  }
}
