package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "planned_sanctioned_wells")
public class PlannedSanctionedWells implements SubmissionDetailChildEntity {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private BigDecimal numberOfPlannedSanctionedWells;

  private BigDecimal totalCapex;

  private BigDecimal numberOfInjectors;

  private BigDecimal numberOfProducers;

  private BigDecimal numberOfMonitors;

  private Boolean anyHorizontalWells;

  private BigDecimal numberOfHorizontalWells;

  private BigDecimal numberOfHorizontalWellsWithErd;

  private Boolean anyHighPressureTemperatureWells;

  private BigDecimal numberOfHighPressureTemperatureWells;

  private Boolean anyWellsWithinNarrowOrTightPpfgWindow;

  private BigDecimal numberOfWellsWithinNarrowOrTightPpfgWindow;

  private Boolean isManagedPressureDrillingUtilized;

  private Boolean anyWellboreStabilityChallenges;

  private BigDecimal numberOfWellboreStabilityChallenges;

  private String stabilityChallengesInformation;

  private Boolean anyWellsExpectedToCrossFaults;

  private BigDecimal numberOfWellsExpectedToCrossFaults;

  private Boolean anyExpectedLossesFromDrilling;

  private String whyLossesExpected;

  private Boolean anyExpectedDrillingChallenges;

  private String drillingChallengesMitigations;

  @Enumerated(EnumType.STRING)
  private StimulationType stimulationTypeRequired;

  private BigDecimal numberOfWellsRequiringStimulation;

  private Boolean isExpectingFormationDamage;

  private String expectedFormationDamage;

  private Boolean anyHeavyOil;

  private Boolean anySpecialisedDrillingEquipmentRequired;

  private String specialisedDrillingEquipmentRequired;

  private Boolean anyMultilateralWells;

  private BigDecimal numberOfMultilateralWells;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public BigDecimal getNumberOfPlannedSanctionedWells() {
    return numberOfPlannedSanctionedWells;
  }

  public void setNumberOfPlannedSanctionedWells(BigDecimal numberOfPlannedSanctionedWells) {
    this.numberOfPlannedSanctionedWells = numberOfPlannedSanctionedWells;
  }

  public BigDecimal getTotalCapex() {
    return totalCapex;
  }

  public void setTotalCapex(BigDecimal totalCapex) {
    this.totalCapex = totalCapex;
  }

  public BigDecimal getNumberOfInjectors() {
    return numberOfInjectors;
  }

  public void setNumberOfInjectors(BigDecimal numberOfInjectors) {
    this.numberOfInjectors = numberOfInjectors;
  }

  public BigDecimal getNumberOfProducers() {
    return numberOfProducers;
  }

  public void setNumberOfProducers(BigDecimal numberOfProducers) {
    this.numberOfProducers = numberOfProducers;
  }

  public BigDecimal getNumberOfMonitors() {
    return numberOfMonitors;
  }

  public void setNumberOfMonitors(BigDecimal numberOfMonitors) {
    this.numberOfMonitors = numberOfMonitors;
  }

  public Boolean getAnyHorizontalWells() {
    return anyHorizontalWells;
  }

  public void setAnyHorizontalWells(Boolean anyHorizontalWells) {
    this.anyHorizontalWells = anyHorizontalWells;
  }

  public BigDecimal getNumberOfHorizontalWells() {
    return numberOfHorizontalWells;
  }

  public void setNumberOfHorizontalWells(BigDecimal numberOfHorizontalWells) {
    this.numberOfHorizontalWells = numberOfHorizontalWells;
  }

  public BigDecimal getNumberOfHorizontalWellsWithErd() {
    return numberOfHorizontalWellsWithErd;
  }

  public void setNumberOfHorizontalWellsWithErd(BigDecimal numberOfHorizontalWellsWithErd) {
    this.numberOfHorizontalWellsWithErd = numberOfHorizontalWellsWithErd;
  }

  public Boolean getAnyHighPressureTemperatureWells() {
    return anyHighPressureTemperatureWells;
  }

  public void setAnyHighPressureTemperatureWells(Boolean anyHighPressureTemperatureWells) {
    this.anyHighPressureTemperatureWells = anyHighPressureTemperatureWells;
  }

  public BigDecimal getNumberOfHighPressureTemperatureWells() {
    return numberOfHighPressureTemperatureWells;
  }

  public void setNumberOfHighPressureTemperatureWells(BigDecimal numberOfHighPressureTemperatureWells) {
    this.numberOfHighPressureTemperatureWells = numberOfHighPressureTemperatureWells;
  }

  public Boolean getAnyWellsWithinNarrowOrTightPpfgWindow() {
    return anyWellsWithinNarrowOrTightPpfgWindow;
  }

  public void setAnyWellsWithinNarrowOrTightPpfgWindow(Boolean anyWellsWithinNarrowOrTightPpfgWindow) {
    this.anyWellsWithinNarrowOrTightPpfgWindow = anyWellsWithinNarrowOrTightPpfgWindow;
  }

  public BigDecimal getNumberOfWellsWithinNarrowOrTightPpfgWindow() {
    return numberOfWellsWithinNarrowOrTightPpfgWindow;
  }

  public void setNumberOfWellsWithinNarrowOrTightPpfgWindow(BigDecimal numberOfWellsWithinNarrowOrTightPpfgWindow) {
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

  public BigDecimal getNumberOfWellboreStabilityChallenges() {
    return numberOfWellboreStabilityChallenges;
  }

  public void setNumberOfWellboreStabilityChallenges(BigDecimal numberOfWellboreStabilityChallenges) {
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

  public BigDecimal getNumberOfWellsExpectedToCrossFaults() {
    return numberOfWellsExpectedToCrossFaults;
  }

  public void setNumberOfWellsExpectedToCrossFaults(BigDecimal numberOfWellsExpectedToCrossFaults) {
    this.numberOfWellsExpectedToCrossFaults = numberOfWellsExpectedToCrossFaults;
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

  public StimulationType getStimulationTypeRequired() {
    return stimulationTypeRequired;
  }

  public void setStimulationTypeRequired(StimulationType stimulationTypeRequired) {
    this.stimulationTypeRequired = stimulationTypeRequired;
  }

  public BigDecimal getNumberOfWellsRequiringStimulation() {
    return numberOfWellsRequiringStimulation;
  }

  public void setNumberOfWellsRequiringStimulation(BigDecimal numberOfWellsRequiringStimulation) {
    this.numberOfWellsRequiringStimulation = numberOfWellsRequiringStimulation;
  }

  public Boolean getIsExpectingFormationDamage() {
    return isExpectingFormationDamage;
  }

  public void setExpectingFormationDamage(Boolean expectingFormationDamage) {
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

  public BigDecimal getNumberOfMultilateralWells() {
    return numberOfMultilateralWells;
  }

  public void setNumberOfMultilateralWells(BigDecimal numberOfMultilateralWells) {
    this.numberOfMultilateralWells = numberOfMultilateralWells;
  }
}
