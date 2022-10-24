package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.BETA;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.CONSTANT;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.CUSTOM;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.LOGNORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.NORMAL;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.NOT_CALCULATED;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.RECTANGULAR;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType.TRIANGULAR;

import java.util.List;

public enum ProbabilityDistributionFieldInstance {
  DEPTH_TO_CREST_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS
  ),
  DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS
  ),
  AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.M_TVT
  ),
  GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.M_TVT
  ),
  GROSS_ROCK_VOLUME(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.MILLION_M3
  ),
  PERCENT_OF_GRV_OIL_BEARING(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  NET_TO_GROSS_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  NET_POROSITY_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.KM2
  ),
  DEPTH_TO_HYDROCARBON_CONTACT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS
  ),
  GAS_HYDROCARBON_SATURATION(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  GAS_EXPANSION_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.SCF_RCF
  ),
  CONDENSATE_GAS_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.STB_MCF
  ),
  GAS_RECOVERY_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  OIL_RECOVERY_FACTOR(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RB_STB
  ),
  OIL_HYDROCARBON_SATURATION(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.SCF_STB
  ),
  MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RB_STB
  ),
  MIXED_OIL_HYDROCARBON_SATURATION(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  MIXED_GAS_HYDROCARBON_SATURATION(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.SCF_STB
  ),
  MIXED_GAS_EXPANSION_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.SCF_RCF
  ),
  MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.STB_MCF
  ),
  MIXED_GAS_RECOVERY_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  MIXED_OIL_RECOVERY_FACTOR(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT
  ),
  PRODUCTIVE_AREA(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.KM2
  ),
  OIL_OUTPUTS_OIL_IN_PLACE(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB
  ),
  OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB
  ),
  OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF
  ),
  OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE
  ),
  GAS_OUTPUTS_GAS_IN_PLACE(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF
  ),
  GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF
  ),
  GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB
  ),
  GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE
  ),
  MIXED_CASE_OIL_IN_PLACE(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB
  ),
  MIXED_CASE_OIL_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB
  ),
  MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF
  ),
  MIXED_CASE_GAS_IN_PLACE(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF
  ),
  MIXED_CASE_GAS_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF
  ),
  MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB
  ),
  MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES(
      ResourceOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE
  )
  ;
  private Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour;
  private final List<ProbabilityDistributionType> presetTypes;

  private ProbabilityOfExceedanceUnits units;

  private final Type type;

  private enum Type {
    PRESET_LIST, CUSTOM
  }

  ProbabilityDistributionFieldInstance(
      List<ProbabilityDistributionType> presetTypes,
      ProbabilityOfExceedanceUnits units) {
    this.presetTypes = presetTypes;
    this.units = units;

    type = Type.PRESET_LIST;
  }

  ProbabilityDistributionFieldInstance(Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour,
                                       ProbabilityOfExceedanceUnits units) {
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
