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

  // Trap properties
  DEPTH_TO_CREST_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to crest"
  ),
  DEPTH_TO_CREST_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to crest"
  ),
  DEPTH_TO_SPILL_POINT_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to spill point"
  ),
  DEPTH_TO_SPILL_POINT_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to spill point"
  ),
  PRODUCTIVE_AREA_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.KM2,
      "Productive area"
  ),
  PRODUCTIVE_AREA_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.KM2,
      "Productive area"
  ),

  // Reservoir properties
  AVERAGE_NET_PAY_THICKNESS_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.M_TVT,
      "Average net pay thickness"
  ),
  AVERAGE_NET_PAY_THICKNESS_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.M_TVT,
      "Average net pay thickness"
  ),
  AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL(
      List.of(CONSTANT),
      ProbabilityOfExceedanceUnits.M_TVT,
      "Average net reservoir thickness"
  ),
  GROSS_RESERVOIR_THICKNESS_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.M_TVT,
      "Gross reservoir thickness"
  ),
  GROSS_RESERVOIR_THICKNESS_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.M_TVT,
      "Gross reservoir thickness"
  ),
  GROSS_ROCK_VOLUME_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.MILLION_M3,
      "Gross rock volume (GRV)"
  ),
  GROSS_ROCK_VOLUME_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.MILLION_M3,
      "Gross rock volume (GRV)"
  ),
  PERCENT_OF_GRV_OIL_BEARING_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.PERCENT,
      "% of GRV which is oil bearing"
  ),
  PERCENT_OF_GRV_OIL_BEARING_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.PERCENT,
      "% of GRV which is oil bearing"
  ),
  NET_TO_GROSS_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Net to gross"
  ),
  NET_TO_GROSS_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Net to gross"
  ),
  NET_POROSITY_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Net porosity"
  ),
  NET_POROSITY_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Net porosity"
  ),
  DEPTH_TO_HYDROCARBON_CONTACT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to hydrocarbon contact"
  ),
  DEPTH_TO_OIL_WATER_CONTACT(
      MinimumMostLikelyMaxBehaviour.class,
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to oil water contact"
  ),
  DEPTH_TO_GAS_WATER_CONTACT(
      MinimumMostLikelyMaxBehaviour.class,
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to gas water contact"
  ),
  DEPTH_TO_GAS_OIL_CONTACT(
      MinimumMostLikelyMaxBehaviour.class,
      ProbabilityOfExceedanceUnits.FT_TVDSS,
      "Depth to gas-oil contact"
  ),

  // Gas case volumetrics
  GAS_HYDROCARBON_SATURATION_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Gas hydrocarbon saturation"
  ),
  GAS_HYDROCARBON_SATURATION_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Gas hydrocarbon saturation"
  ),
  GAS_EXPANSION_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.SCF_RCF,
      "Gas expansion factor"
  ),
  CONDENSATE_GAS_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.STB_MCF,
      "Condensate-gas ratio life of field"
  ),
  GAS_RECOVERY_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Gas recovery factor"
  ),
  GAS_FORMATION_VOLUME_FACTOR_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RCF_SCF,
      "Gas formation volume factor (Bg)"
  ),
  GAS_FORMATION_VOLUME_FACTOR_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RCF_SCF,
      "Gas formation volume factor (Bg)"
  ),

  // Oil case volumetrics
  OIL_RECOVERY_FACTOR_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil recovery factor"
  ),
  OIL_RECOVERY_FACTOR_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil recovery factor"
  ),
  OIL_RECOVERY_FACTOR_FIELD(
      List.of(CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil recovery factor"
  ),
  OIL_FORMATION_VOLUME_FACTOR_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RB_STB,
      "Oil formation volume factor (Bo)"
  ),
  OIL_FORMATION_VOLUME_FACTOR_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RB_STB,
      "Oil formation volume factor (Bo)"
  ),
  OIL_FORMATION_VOLUME_FACTOR_FIELD(
      List.of(CONSTANT),
      ProbabilityOfExceedanceUnits.RB_STB,
      "Oil formation volume factor (Bo)"
  ),
  OIL_HYDROCARBON_SATURATION_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil hydrocarbon saturation"
  ),
  OIL_HYDROCARBON_SATURATION_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil hydrocarbon saturation"
  ),
  OIL_HYDROCARBON_SATURATION_FIELD(
      MinimumMostLikelyMaxBehaviour.class,
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil hydrocarbon saturation"
  ),
  ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.SCF_STB,
      "Associated gas-oil ratio life of field"
  ),
  ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT(
      List.of(CONSTANT),
      ProbabilityOfExceedanceUnits.SCF_STB,
      "Associated gas-oil ratio life of field"
  ),

  // Mixed case volumetrics
  MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RB_STB,
      "Oil Formation volume factor (Bo)"
  ),
  MIXED_OIL_FORMATION_VOLUME_FACTOR_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RB_STB,
      "Oil Formation volume factor (Bo)"
  ),
  MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RCF_SCF,
      "Gas formation volume factor (Bg)"
  ),
  MIXED_GAS_FORMATION_VOLUME_FACTOR_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.RCF_SCF,
      "Gas formation volume factor (Bg)"
  ),
  MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil hydrocarbon saturation"
  ),
  MIXED_OIL_HYDROCARBON_SATURATION_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil hydrocarbon saturation"
  ),
  MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Gas hydrocarbon saturation"
  ),
  MIXED_GAS_HYDROCARBON_SATURATION_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Gas hydrocarbon saturation"
  ),
  MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.SCF_STB,
      "Associated gas-oil ratio life of field"
  ),
  MIXED_GAS_EXPANSION_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.SCF_RCF,
      "Gas expansion factor"
  ),
  MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT, NOT_CALCULATED),
      ProbabilityOfExceedanceUnits.STB_MCF,
      "Condensate-gas ratio life of field"
  ),
  MIXED_GAS_RECOVERY_FACTOR(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Gas recovery factor"
  ),
  MIXED_OIL_RECOVERY_FACTOR_PROSPECT(
      List.of(NORMAL, LOGNORMAL, BETA, TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil recovery factor"
  ),
  MIXED_OIL_RECOVERY_FACTOR_DISCOVERY(
      List.of(TRIANGULAR, RECTANGULAR, CONSTANT),
      ProbabilityOfExceedanceUnits.PERCENT,
      "Oil recovery factor"
  ),

  // Oil case outputs
  OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil in place"
  ),
  OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil in place"
  ),
  OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil recoverable resources"
  ),
  OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil recoverable resources"
  ),
  OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Associated gas recoverable resources"
  ),
  OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Associated gas recoverable resources"
  ),
  OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE,
      "Oil equivalent recoverable resources"
  ),
  OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE,
      "Oil equivalent recoverable resources"
  ),

  // Gas case outputs
  GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas in place"
  ),
  GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas in place"
  ),
  GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas recoverable resources"
  ),
  GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas recoverable resources"
  ),
  GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Associated condensate recoverable resources"
  ),
  GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Associated condensate recoverable resources"
  ),
  GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE,
      "Oil equivalent recoverable resources"
  ),
  GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE,
      "Oil equivalent recoverable resources"
  ),

  // Mixed case outputs
  MIXED_CASE_OIL_IN_PLACE_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil in place"
  ),
  MIXED_CASE_OIL_IN_PLACE_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil in place"
  ),
  MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil recoverable resources"
  ),
  MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Oil recoverable resources"
  ),
  MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Associated gas recoverable resources"
  ),
  MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Associated gas recoverable resources"
  ),
  MIXED_CASE_GAS_IN_PLACE_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas in place"
  ),
  MIXED_CASE_GAS_IN_PLACE_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas in place"
  ),
  MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas recoverable resources"
  ),
  MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.BCF,
      "Gas recoverable resources"
  ),
  MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Associated condensate recoverable resources"
  ),
  MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMSTB,
      "Associated condensate recoverable resources"
  ),
  MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT(
      ProspectOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE,
      "Oil equivalent recoverable resources"
  ),
  MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY(
      DiscoveryOutputsPreDrillBehaviour.class,
      ProbabilityOfExceedanceUnits.MMBOE,
      "Oil equivalent recoverable resources"
  ),

  // Field Permeability
  FIELD_PERMEABILITY(
      MinimumMostLikelyMaxBehaviour.class,
      ProbabilityOfExceedanceUnits.MD,
      "Permeability"
  ),
  FIELD_HORIZONTAL_PERMEABILITY(
      MinimumMostLikelyMaxBehaviour.class,
      ProbabilityOfExceedanceUnits.MD,
      "Horizontal permeability"
  ),
  FIELD_VERTICAL_PERMEABILITY(
      MinimumMostLikelyMaxBehaviour.class,
      ProbabilityOfExceedanceUnits.MD,
      "Vertical permeability"
  )
  ;
  private Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour;
  private final List<ProbabilityDistributionType> presetTypes;

  private final ProbabilityOfExceedanceUnits units;

  private final Type type;

  private final String displayName;

  private enum Type {
    PRESET_LIST, CUSTOM
  }

  ProbabilityDistributionFieldInstance(
      List<ProbabilityDistributionType> presetTypes,
      ProbabilityOfExceedanceUnits units,
      String displayName) {
    this.presetTypes = presetTypes;
    this.units = units;

    type = Type.PRESET_LIST;
    this.displayName = displayName;

  }

  ProbabilityDistributionFieldInstance(Class<? extends ProbabilityDistributionTypeBehaviour> customBehaviour,
                                       ProbabilityOfExceedanceUnits units,
                                       String displayName) {
    this.customBehaviour = customBehaviour;
    this.units = units;

    this.presetTypes = List.of(CUSTOM);
    type = Type.CUSTOM;
    this.displayName = displayName;
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

  public String getDisplayName() {
    return displayName;
  }

  public boolean isCustomType() {
    return type == Type.CUSTOM;
  }
}