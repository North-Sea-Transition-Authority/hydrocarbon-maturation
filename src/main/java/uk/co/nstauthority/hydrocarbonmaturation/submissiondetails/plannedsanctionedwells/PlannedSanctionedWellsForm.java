package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class PlannedSanctionedWellsForm implements SubmissionForm {
  private DecimalInput totalNumberOfPlannedSanctionedWells = new DecimalInput();

  private DecimalInput totalCapex = new DecimalInput();

  private DecimalInput numberOfInjectors = new DecimalInput();

  private DecimalInput numberOfProducers = new DecimalInput();

  private DecimalInput numberOfMonitors = new DecimalInput();

  private Boolean anyHorizontalWells;

  private DecimalInput numberOfHorizontalWells = new DecimalInput();

  private DecimalInput numberOfHorizontalWellsWithErd = new DecimalInput();

  private Boolean anyHighPressureTemperatureWells;

  private DecimalInput numberOfHighPressureTemperatureWells = new DecimalInput();

  private Boolean anyWellsWithinNarrowOrTightPpfgWindow;

  private DecimalInput numberOfWellsWithinNarrowOrTightPpfgWindow = new DecimalInput();

  private Boolean isManagedPressureDrillingUtilized;

  private Boolean anyWellboreStabilityChallenges;

  private DecimalInput numberOfWellboreStabilityChallenges = new DecimalInput();

  private String stabilityChallengesInformation;

  private Boolean anyWellsExpectedToCrossFaults;

  private DecimalInput numberOfWellsExpectedToCrossFaults = new DecimalInput();

  private Set<Fault> expectedFaults = Set.of();

  private Boolean anyExpectedLossesFromDrilling;

  private String whyLossesExpected;

  private Boolean anyExpectedDrillingChallenges;

  private String drillingChallengesMitigations;

  private Map<StimulationType, DecimalInput> stimulationTypeDecimalInputHashMap = emptyNumberOfStimulationTypes();
  private StimulationType stimulationTypeRequired;

  private Boolean isExpectingFormationDamage;

  private String expectedFormationDamage;

  private Boolean anyHeavyOil;

  private Boolean anySpecialisedDrillingEquipmentRequired;

  private String specialisedDrillingEquipmentRequired;

  private Boolean anyMultilateralWells;

  private DecimalInput numberOfMultilateralWells = new DecimalInput();

  private Map<HoleCompletionType, Boolean> selectedHoleCompletionTypes = new EnumMap<>(HoleCompletionType.class);
  private Map<HoleCompletionType, DecimalInput> numberOfHoleCompletionTypes = emptyNumberOfHoleCompletionTypes();

  private Map<PlannedCompletionType, Boolean> selectedPlannedCompletionTypes = new EnumMap<>(PlannedCompletionType.class);
  private Map<PlannedCompletionType, DecimalInput> numberOfPlannedCompletionTypes = emptyNumberOfPlannedCompletionTypes();
  private String otherPlannedCompletionType;

  private Map<ArtificialLiftMechanism, Boolean> selectedArtificialLiftMechanisms = new EnumMap<>(ArtificialLiftMechanism.class);
  private Map<ArtificialLiftMechanism, DecimalInput> numberOfArtificialLiftMechanisms = emptyNumberOfArtificialLiftMechanisms();
  private String otherArtificialLiftMechanism;

  public DecimalInput getTotalNumberOfPlannedSanctionedWells() {
    return totalNumberOfPlannedSanctionedWells;
  }

  public void setTotalNumberOfPlannedSanctionedWells(DecimalInput totalNumberOfPlannedSanctionedWells) {
    this.totalNumberOfPlannedSanctionedWells = totalNumberOfPlannedSanctionedWells;
  }

  public DecimalInput getTotalCapex() {
    return totalCapex;
  }

  public void setTotalCapex(DecimalInput totalCapex) {
    this.totalCapex = totalCapex;
  }

  public DecimalInput getNumberOfInjectors() {
    return numberOfInjectors;
  }

  public void setNumberOfInjectors(DecimalInput numberOfInjectors) {
    this.numberOfInjectors = numberOfInjectors;
  }

  public DecimalInput getNumberOfProducers() {
    return numberOfProducers;
  }

  public void setNumberOfProducers(DecimalInput numberOfProducers) {
    this.numberOfProducers = numberOfProducers;
  }

  public DecimalInput getNumberOfMonitors() {
    return numberOfMonitors;
  }

  public void setNumberOfMonitors(DecimalInput numberOfMonitors) {
    this.numberOfMonitors = numberOfMonitors;
  }

  public Boolean getAnyHorizontalWells() {
    return anyHorizontalWells;
  }

  public void setAnyHorizontalWells(Boolean anyHorizontalWells) {
    this.anyHorizontalWells = anyHorizontalWells;
  }

  public DecimalInput getNumberOfHorizontalWells() {
    return numberOfHorizontalWells;
  }

  public void setNumberOfHorizontalWells(DecimalInput numberOfHorizontalWells) {
    this.numberOfHorizontalWells = numberOfHorizontalWells;
  }

  public DecimalInput getNumberOfHorizontalWellsWithErd() {
    return numberOfHorizontalWellsWithErd;
  }

  public void setNumberOfHorizontalWellsWithErd(DecimalInput numberOfHorizontalWellsWithErd) {
    this.numberOfHorizontalWellsWithErd = numberOfHorizontalWellsWithErd;
  }

  public Boolean getAnyHighPressureTemperatureWells() {
    return anyHighPressureTemperatureWells;
  }

  public void setAnyHighPressureTemperatureWells(Boolean anyHighPressureTemperatureWells) {
    this.anyHighPressureTemperatureWells = anyHighPressureTemperatureWells;
  }

  public DecimalInput getNumberOfHighPressureTemperatureWells() {
    return numberOfHighPressureTemperatureWells;
  }

  public void setNumberOfHighPressureTemperatureWells(DecimalInput numberOfHighPressureTemperatureWells) {
    this.numberOfHighPressureTemperatureWells = numberOfHighPressureTemperatureWells;
  }

  public Boolean getAnyWellsWithinNarrowOrTightPpfgWindow() {
    return anyWellsWithinNarrowOrTightPpfgWindow;
  }

  public void setAnyWellsWithinNarrowOrTightPpfgWindow(Boolean anyWellsWithinNarrowOrTightPpfgWindow) {
    this.anyWellsWithinNarrowOrTightPpfgWindow = anyWellsWithinNarrowOrTightPpfgWindow;
  }

  public DecimalInput getNumberOfWellsWithinNarrowOrTightPpfgWindow() {
    return numberOfWellsWithinNarrowOrTightPpfgWindow;
  }

  public void setNumberOfWellsWithinNarrowOrTightPpfgWindow(DecimalInput numberOfWellsWithinNarrowOrTightPpfgWindow) {
    this.numberOfWellsWithinNarrowOrTightPpfgWindow = numberOfWellsWithinNarrowOrTightPpfgWindow;
  }

  public Boolean getIsManagedPressureDrillingUtilized() {
    return isManagedPressureDrillingUtilized;
  }

  public void setIsManagedPressureDrillingUtilized(Boolean managedPressureDrillingUtilized) {
    isManagedPressureDrillingUtilized = managedPressureDrillingUtilized;
  }

  public Boolean getAnyWellboreStabilityChallenges() {
    return anyWellboreStabilityChallenges;
  }

  public void setAnyWellboreStabilityChallenges(Boolean anyWellboreStabilityChallenges) {
    this.anyWellboreStabilityChallenges = anyWellboreStabilityChallenges;
  }

  public DecimalInput getNumberOfWellboreStabilityChallenges() {
    return numberOfWellboreStabilityChallenges;
  }

  public void setNumberOfWellboreStabilityChallenges(DecimalInput numberOfWellboreStabilityChallenges) {
    this.numberOfWellboreStabilityChallenges = numberOfWellboreStabilityChallenges;
  }

  public String getStabilityChallengesInformation() {
    return stabilityChallengesInformation;
  }

  public void setStabilityChallengesInformation(String stabilityChallengesInformation) {
    this.stabilityChallengesInformation = stabilityChallengesInformation;
  }

  public Boolean getAnyWellsExpectedToCrossFaults() {
    return anyWellsExpectedToCrossFaults;
  }

  public void setAnyWellsExpectedToCrossFaults(Boolean anyWellsExpectedToCrossFaults) {
    this.anyWellsExpectedToCrossFaults = anyWellsExpectedToCrossFaults;
  }

  public DecimalInput getNumberOfWellsExpectedToCrossFaults() {
    return numberOfWellsExpectedToCrossFaults;
  }

  public void setNumberOfWellsExpectedToCrossFaults(DecimalInput numberOfWellsExpectedToCrossFaults) {
    this.numberOfWellsExpectedToCrossFaults = numberOfWellsExpectedToCrossFaults;
  }

  public Set<Fault> getExpectedFaults() {
    return expectedFaults;
  }

  public void setExpectedFaults(Set<Fault> expectedFaults) {
    this.expectedFaults = expectedFaults;
  }

  public Boolean getAnyExpectedLossesFromDrilling() {
    return anyExpectedLossesFromDrilling;
  }

  public void setAnyExpectedLossesFromDrilling(Boolean anyExpectedLossesFromDrilling) {
    this.anyExpectedLossesFromDrilling = anyExpectedLossesFromDrilling;
  }

  public String getWhyLossesExpected() {
    return whyLossesExpected;
  }

  public void setWhyLossesExpected(String whyLossesExpected) {
    this.whyLossesExpected = whyLossesExpected;
  }

  public Boolean getAnyExpectedDrillingChallenges() {
    return anyExpectedDrillingChallenges;
  }

  public void setAnyExpectedDrillingChallenges(Boolean anyExpectedDrillingChallenges) {
    this.anyExpectedDrillingChallenges = anyExpectedDrillingChallenges;
  }

  public String getDrillingChallengesMitigations() {
    return drillingChallengesMitigations;
  }

  public void setDrillingChallengesMitigations(String drillingChallengesMitigations) {
    this.drillingChallengesMitigations = drillingChallengesMitigations;
  }

  public Map<StimulationType, DecimalInput> getStimulationTypeDecimalInputHashMap() {
    return stimulationTypeDecimalInputHashMap;
  }

  public void setStimulationTypeDecimalInputHashMap(Map<StimulationType, DecimalInput> stimulationTypeDecimalInputHashMap) {
    this.stimulationTypeDecimalInputHashMap = stimulationTypeDecimalInputHashMap;
  }

  public StimulationType getStimulationTypeRequired() {
    return stimulationTypeRequired;
  }

  public void setStimulationTypeRequired(StimulationType stimulationTypeRequired) {
    this.stimulationTypeRequired = stimulationTypeRequired;
  }

  public Boolean getIsExpectingFormationDamage() {
    return isExpectingFormationDamage;
  }

  public void setIsExpectingFormationDamage(Boolean expectingFormationDamage) {
    isExpectingFormationDamage = expectingFormationDamage;
  }

  public String getExpectedFormationDamage() {
    return expectedFormationDamage;
  }

  public void setExpectedFormationDamage(String expectedFormationDamage) {
    this.expectedFormationDamage = expectedFormationDamage;
  }

  public Boolean getAnyHeavyOil() {
    return anyHeavyOil;
  }

  public void setAnyHeavyOil(Boolean anyHeavyOil) {
    this.anyHeavyOil = anyHeavyOil;
  }

  public Boolean getAnySpecialisedDrillingEquipmentRequired() {
    return anySpecialisedDrillingEquipmentRequired;
  }

  public void setAnySpecialisedDrillingEquipmentRequired(Boolean anySpecialisedDrillingEquipmentRequired) {
    this.anySpecialisedDrillingEquipmentRequired = anySpecialisedDrillingEquipmentRequired;
  }

  public String getSpecialisedDrillingEquipmentRequired() {
    return specialisedDrillingEquipmentRequired;
  }

  public void setSpecialisedDrillingEquipmentRequired(String specialisedDrillingEquipmentRequired) {
    this.specialisedDrillingEquipmentRequired = specialisedDrillingEquipmentRequired;
  }

  public Boolean getAnyMultilateralWells() {
    return anyMultilateralWells;
  }

  public void setAnyMultilateralWells(Boolean anyMultilateralWells) {
    this.anyMultilateralWells = anyMultilateralWells;
  }

  public DecimalInput getNumberOfMultilateralWells() {
    return numberOfMultilateralWells;
  }

  public void setNumberOfMultilateralWells(DecimalInput numberOfMultilateralWells) {
    this.numberOfMultilateralWells = numberOfMultilateralWells;
  }

  public Map<HoleCompletionType, Boolean> getSelectedHoleCompletionTypes() {
    return selectedHoleCompletionTypes;
  }

  public void setSelectedHoleCompletionTypes(Map<HoleCompletionType, Boolean> selectedHoleCompletionTypes) {
    this.selectedHoleCompletionTypes = selectedHoleCompletionTypes;
  }

  public Map<HoleCompletionType, DecimalInput> getNumberOfHoleCompletionTypes() {
    return numberOfHoleCompletionTypes;
  }

  public void setNumberOfHoleCompletionTypes(Map<HoleCompletionType, DecimalInput> numberOfHoleCompletionTypes) {
    this.numberOfHoleCompletionTypes = numberOfHoleCompletionTypes;
  }

  public Map<PlannedCompletionType, Boolean> getSelectedPlannedCompletionTypes() {
    return selectedPlannedCompletionTypes;
  }

  public void setSelectedPlannedCompletionTypes(Map<PlannedCompletionType, Boolean> selectedPlannedCompletionTypes) {
    this.selectedPlannedCompletionTypes = selectedPlannedCompletionTypes;
  }

  public Map<PlannedCompletionType, DecimalInput> getNumberOfPlannedCompletionTypes() {
    return numberOfPlannedCompletionTypes;
  }

  public void setNumberOfPlannedCompletionTypes(Map<PlannedCompletionType, DecimalInput> numberOfPlannedCompletionTypes) {
    this.numberOfPlannedCompletionTypes = numberOfPlannedCompletionTypes;
  }

  public String getOtherPlannedCompletionType() {
    return otherPlannedCompletionType;
  }

  public void setOtherPlannedCompletionType(String otherPlannedCompletionType) {
    this.otherPlannedCompletionType = otherPlannedCompletionType;
  }

  public Map<ArtificialLiftMechanism, Boolean> getSelectedArtificialLiftMechanisms() {
    return selectedArtificialLiftMechanisms;
  }

  public void setSelectedArtificialLiftMechanisms(Map<ArtificialLiftMechanism, Boolean> selectedArtificialLiftMechanisms) {
    this.selectedArtificialLiftMechanisms = selectedArtificialLiftMechanisms;
  }

  public Map<ArtificialLiftMechanism, DecimalInput> getNumberOfArtificialLiftMechanisms() {
    return numberOfArtificialLiftMechanisms;
  }

  public void setNumberOfArtificialLiftMechanisms(Map<ArtificialLiftMechanism, DecimalInput> numberOfArtificialLiftMechanisms) {
    this.numberOfArtificialLiftMechanisms = numberOfArtificialLiftMechanisms;
  }

  public String getOtherArtificialLiftMechanism() {
    return otherArtificialLiftMechanism;
  }

  public void setOtherArtificialLiftMechanism(String otherArtificialLiftMechanism) {
    this.otherArtificialLiftMechanism = otherArtificialLiftMechanism;
  }

  public static Map<StimulationType, DecimalInput> emptyNumberOfStimulationTypes() {
    var map = new EnumMap<StimulationType, DecimalInput>(StimulationType.class);
    Arrays.stream(StimulationType.values())
        .forEach(stimulationType -> map.put(stimulationType, new DecimalInput()));
    return map;
  }

  public static Map<HoleCompletionType, DecimalInput> emptyNumberOfHoleCompletionTypes() {
    var map = new EnumMap<HoleCompletionType, DecimalInput>(HoleCompletionType.class);
    Arrays.stream(HoleCompletionType.values())
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .forEach(holeCompletionType -> map.put(holeCompletionType, new DecimalInput()));
    return map;
  }

  public static Map<PlannedCompletionType, DecimalInput> emptyNumberOfPlannedCompletionTypes() {
    var map = new EnumMap<PlannedCompletionType, DecimalInput>(PlannedCompletionType.class);
    Arrays.stream(PlannedCompletionType.values())
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .forEach(plannedCompletionType -> map.put(plannedCompletionType, new DecimalInput()));
    return map;
  }

  public static Map<ArtificialLiftMechanism, DecimalInput> emptyNumberOfArtificialLiftMechanisms() {
    var map = new EnumMap<ArtificialLiftMechanism, DecimalInput>(ArtificialLiftMechanism.class);
    Arrays.stream(ArtificialLiftMechanism.values())
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .forEach(holeCompletionType -> map.put(holeCompletionType, new DecimalInput()));
    return map;
  }
}
