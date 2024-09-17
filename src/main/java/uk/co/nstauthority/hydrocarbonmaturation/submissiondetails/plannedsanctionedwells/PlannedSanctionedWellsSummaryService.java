package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.StringUtil;

@Service
public class PlannedSanctionedWellsSummaryService {
  public static final String PLANNED_SANCTIONED_WELLS = "Planned sanctioned wells";
  public static final String HOLE_COMPLETION_TYPES = "Planned sanctioned wells - hole completion types";
  public static final String PLANNED_COMPLETION_TYPE = "Planned sanctioned wells - planned completion types";
  public static final String ARTIFICIAL_LIFT_MECHANISMS = "Planned sanctioned wells - artificial lift mechanisms";
  public static final String NUMBER_OF_WELLS = "Number of wells";

  public static final String TOTAL_CAPEX = "Total CAPEX for all planned sanctioned wells";
  public static final String TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS = "Total number of planned sanctioned wells";
  public static final String NUMBER_OF_INJECTORS = "Number of injectors";
  public static final String NUMBER_OF_PRODUCERS = "Number of producers";
  public static final String NUMBER_OF_MONITORS = "Number of monitors";
  public static final String NUMBER_OF_HORIZONTAL_WELLS = "Number of horizontal wells";
  public static final String NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD = "Number of horizontal wells with extended reach drilling";
  public static final String NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS = "Number of high pressure or high temperature wells";
  public static final String NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW = "Number of wells within a narrow or" +
      " tight pore pressure fracture gradient (PPFG) window";
  public static final String IS_MANAGED_PRESSURE_DRILLING_UTILIZED = "Is managed pressure drilling (MPD) utilised?";
  public static final String NUMBER_OF_WELLBORE_STABILITY_CHALLENGES = "Number of wells with wellbore stability challenges";
  public static final String STABILITY_CHALLENGES_INFORMATION = "Provide information on stability challenges";
  public static final String NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS = "Number of wells expected to cross a fault";
  public static final String EXPECTED_FAULTS = "Where are faults expected?";
  public static final String ANY_EXPECTED_LOSSES_FROM_DRILLING = "Do you expect to incur losses in drilling these wells?";
  public static final String WHY_LOSSES_EXPECTED = "Why are losses expected?";
  public static final String ANY_EXPECTED_DRILLING_CHALLENGES = "Do you expect to encounter drilling challenges?";
  public static final String DRILLING_CHALLENGES_MITIGATIONS = "What mitigations do you have in place?";
  public static final String STIMULATION_TYPE_REQUIRED = "What type of well stimulation is required?";
  public static final String NUMBER_OF_WELLS_REQUIRING_STIMULATION = "Number of wells requiring stimulation";
  public static final String IS_EXPECTING_FORMATION_DAMAGE = "Do you envisage any formation damage?";
  public static final String EXPECTED_FORMATION_DAMAGE = "Describe the type of formation damage expected";
  public static final String ANY_HEAVY_OIL = "Is there heavy oil with an API below 20 degrees?";
  public static final String ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED = "Are specialised drilling equipment or services" +
      " required?";
  public static final String SPECIALISED_DRILLING_EQUIPMENT_REQUIRED = "What specialised drilling equipment or services" +
      " are required?";
  public static final String NUMBER_OF_MULTILATERAL_WELLS = "Number of multilateral wells";

  private final PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  private final PswFaultRepository pswFaultRepository;
  private final HoleCompletionTypesRepository holeCompletionTypesRepository;
  private final PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  private final ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;

  @Autowired
  public PlannedSanctionedWellsSummaryService(PlannedSanctionedWellsRepository plannedSanctionedWellsRepository,
                                              PswFaultRepository pswFaultRepository,
                                              HoleCompletionTypesRepository holeCompletionTypesRepository,
                                              PlannedCompletionTypesRepository plannedCompletionTypesRepository,
                                              ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository) {
    this.plannedSanctionedWellsRepository = plannedSanctionedWellsRepository;
    this.pswFaultRepository = pswFaultRepository;
    this.holeCompletionTypesRepository = holeCompletionTypesRepository;
    this.plannedCompletionTypesRepository = plannedCompletionTypesRepository;
    this.artificialLiftMechanismsRepository = artificialLiftMechanismsRepository;
  }

  public SummaryCard getPlannedSanctionedWellsCard(SubmissionDetail submissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail).ifPresentOrElse(
        plannedSanctionedWells -> {
          summaryDataView.addKeyValue(TOTAL_CAPEX, getFormattedTotalCapex(plannedSanctionedWells));
          summaryDataView.addKeyValue(TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS,
              plannedSanctionedWells.getNumberOfPlannedSanctionedWells());
          summaryDataView.addKeyValue(NUMBER_OF_INJECTORS, plannedSanctionedWells.getNumberOfInjectors());
          summaryDataView.addKeyValue(NUMBER_OF_PRODUCERS, plannedSanctionedWells.getNumberOfProducers());
          summaryDataView.addKeyValue(NUMBER_OF_MONITORS, plannedSanctionedWells.getNumberOfMonitors());

          var numberOfHorizontalWells = getNumberOfWells(
              plannedSanctionedWells.getAnyHorizontalWells(),
              plannedSanctionedWells.getNumberOfHorizontalWells()
          );
          summaryDataView.addKeyValue(NUMBER_OF_HORIZONTAL_WELLS, numberOfHorizontalWells);

          var numberOfHorizontalWellsWithErd = getNumberOfWells(
              plannedSanctionedWells.getAnyHorizontalWells(),
              plannedSanctionedWells.getNumberOfHorizontalWellsWithErd()
          );
          summaryDataView.addKeyValue(NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD, numberOfHorizontalWellsWithErd);

          var numberOfHighPressureTemperatureWells = getNumberOfWells(
              plannedSanctionedWells.getAnyHighPressureTemperatureWells(),
              plannedSanctionedWells.getNumberOfHighPressureTemperatureWells()
          );
          summaryDataView.addKeyValue(NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS, numberOfHighPressureTemperatureWells);

          var numberOfWellsWithinNarrowOrTightPpfgWindow = getNumberOfWells(
              plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow(),
              plannedSanctionedWells.getNumberOfWellsWithinNarrowOrTightPpfgWindow()
          );
          summaryDataView.addKeyValue(NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
              numberOfWellsWithinNarrowOrTightPpfgWindow);
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow())) {
            summaryDataView.addKeyValue(IS_MANAGED_PRESSURE_DRILLING_UTILIZED,
                plannedSanctionedWells.getIsManagedPressureDrillingUtilized());
          }

          var numberOfWellsWithWellboreStabilityChallenges = getNumberOfWells(
              plannedSanctionedWells.getAnyWellboreStabilityChallenges(),
              plannedSanctionedWells.getNumberOfWellboreStabilityChallenges()
          );
          summaryDataView.addKeyValue(NUMBER_OF_WELLBORE_STABILITY_CHALLENGES,
              numberOfWellsWithWellboreStabilityChallenges);
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellboreStabilityChallenges())) {
            summaryDataView.addKeyValue(STABILITY_CHALLENGES_INFORMATION,
                plannedSanctionedWells.getStabilityChallengesInformation());
          }

          var numberOfWellsExpectedToCrossFaults = getNumberOfWells(
              plannedSanctionedWells.getAnyWellsExpectedToCrossFaults(),
              plannedSanctionedWells.getNumberOfWellsExpectedToCrossFaults()
          );
          summaryDataView.addKeyValue(NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS, numberOfWellsExpectedToCrossFaults);
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellsExpectedToCrossFaults())) {
            summaryDataView.addKeyValue(EXPECTED_FAULTS, getExpectedFaultsAsString(plannedSanctionedWells));
          }

          summaryDataView.addKeyValue(ANY_EXPECTED_LOSSES_FROM_DRILLING,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyExpectedLossesFromDrilling()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyExpectedLossesFromDrilling())) {
            summaryDataView.addKeyValue(WHY_LOSSES_EXPECTED,
                plannedSanctionedWells.getWhyLossesExpected());
          }

          summaryDataView.addKeyValue(ANY_EXPECTED_DRILLING_CHALLENGES,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyExpectedDrillingChallenges()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyExpectedDrillingChallenges())) {
            summaryDataView.addKeyValue(DRILLING_CHALLENGES_MITIGATIONS,
                plannedSanctionedWells.getDrillingChallengesMitigations());
          }

          var stimulatedTypeRequired = Objects.nonNull(plannedSanctionedWells.getStimulationTypeRequired())
              ? plannedSanctionedWells.getStimulationTypeRequired().getDisplayName()
              : "";
          summaryDataView.addKeyValue(STIMULATION_TYPE_REQUIRED, stimulatedTypeRequired);
          if (StimulationType.MATRIX_ACIDISING.equals(plannedSanctionedWells.getStimulationTypeRequired())
              || StimulationType.HYDRAULIC_FRACTURING.equals(plannedSanctionedWells.getStimulationTypeRequired())) {
            summaryDataView.addKeyValue(NUMBER_OF_WELLS_REQUIRING_STIMULATION,
                plannedSanctionedWells.getNumberOfWellsRequiringStimulation());
          }

          summaryDataView.addKeyValue(IS_EXPECTING_FORMATION_DAMAGE,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getIsExpectingFormationDamage()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getIsExpectingFormationDamage())) {
            summaryDataView.addKeyValue(EXPECTED_FORMATION_DAMAGE, plannedSanctionedWells.getExpectedFormationDamage());
          }

          summaryDataView.addKeyValue(ANY_HEAVY_OIL, plannedSanctionedWells.getAnyHeavyOil());

          summaryDataView.addKeyValue(ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnySpecialisedDrillingEquipmentRequired()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnySpecialisedDrillingEquipmentRequired())) {
            summaryDataView.addKeyValue(SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
                plannedSanctionedWells.getSpecialisedDrillingEquipmentRequired());
          }

          var numberOfMultilateralWells = getNumberOfWells(
              plannedSanctionedWells.getAnyMultilateralWells(),
              plannedSanctionedWells.getNumberOfMultilateralWells()
          );
          summaryDataView.addKeyValue(NUMBER_OF_MULTILATERAL_WELLS, numberOfMultilateralWells);
        },
        () -> {
          summaryDataView.addKeyValue(TOTAL_CAPEX, "");
          summaryDataView.addKeyValue(TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS, "");
          summaryDataView.addKeyValue(NUMBER_OF_INJECTORS, "");
          summaryDataView.addKeyValue(NUMBER_OF_PRODUCERS, "");
          summaryDataView.addKeyValue(NUMBER_OF_MONITORS, "");
          summaryDataView.addKeyValue(NUMBER_OF_HORIZONTAL_WELLS, "");
          summaryDataView.addKeyValue(NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD, "");
          summaryDataView.addKeyValue(NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS, "");
          summaryDataView.addKeyValue(NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW, "");
          summaryDataView.addKeyValue(IS_MANAGED_PRESSURE_DRILLING_UTILIZED, "");
          summaryDataView.addKeyValue(NUMBER_OF_WELLBORE_STABILITY_CHALLENGES, "");
          summaryDataView.addKeyValue(STABILITY_CHALLENGES_INFORMATION, "");
          summaryDataView.addKeyValue(NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS, "");
          summaryDataView.addKeyValue(EXPECTED_FAULTS, "");
          summaryDataView.addKeyValue(ANY_EXPECTED_LOSSES_FROM_DRILLING, "");
          summaryDataView.addKeyValue(WHY_LOSSES_EXPECTED, "");
          summaryDataView.addKeyValue(ANY_EXPECTED_DRILLING_CHALLENGES, "");
          summaryDataView.addKeyValue(DRILLING_CHALLENGES_MITIGATIONS, "");
          summaryDataView.addKeyValue(STIMULATION_TYPE_REQUIRED, "");
          summaryDataView.addKeyValue(NUMBER_OF_WELLS_REQUIRING_STIMULATION, "");
          summaryDataView.addKeyValue(IS_EXPECTING_FORMATION_DAMAGE, "");
          summaryDataView.addKeyValue(EXPECTED_FORMATION_DAMAGE, "");
          summaryDataView.addKeyValue(ANY_HEAVY_OIL, "");
          summaryDataView.addKeyValue(ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED, "");
          summaryDataView.addKeyValue(SPECIALISED_DRILLING_EQUIPMENT_REQUIRED, "");
          summaryDataView.addKeyValue(NUMBER_OF_MULTILATERAL_WELLS, "");
        });

    return SummaryCard.simpleSummaryCardWithHeading(PLANNED_SANCTIONED_WELLS, summaryDataView);
  }

  public SummaryCard getHoleCompletionTypesSummaryCard(SubmissionDetail submissionDetail) {
    var summaryTableView = SummaryTableView.newWithHeading("", NUMBER_OF_WELLS);
    plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail).ifPresent(
        plannedSanctionedWells ->
            holeCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
                .stream()
                .sorted(Comparator.comparingInt(holeCompletionTypes ->
                    holeCompletionTypes.getHoleCompletionType().getDisplayOrder()))
                .forEach(
                    holeCompletionTypes ->
                        summaryTableView.addRow(
                            holeCompletionTypes.getHoleCompletionType().getDisplayName(),
                            holeCompletionTypes.getNumberOfWellsWithHoleCompletionType())
                ));

    return SummaryCard.tableFixedWidthSummaryCardWithHeading(HOLE_COMPLETION_TYPES, summaryTableView);
  }

  public SummaryCard getPlannedCompletionTypesSummaryCard(SubmissionDetail submissionDetail) {
    var summaryTableView = SummaryTableView.newWithHeading("", NUMBER_OF_WELLS);
    plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail).ifPresent(
        plannedSanctionedWells ->
            plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
                .stream()
                .sorted(Comparator.comparingInt(plannedCompletionTypes ->
                    plannedCompletionTypes.getPlannedCompletionType().getDisplayOrder()))
                .forEach(
                    plannedCompletionTypes -> {
                      var typeOrOther = plannedCompletionTypes.getPlannedCompletionType() == PlannedCompletionType.OTHER
                          ? plannedCompletionTypes.getOtherPlannedCompletionType()
                          : plannedCompletionTypes.getPlannedCompletionType().getDisplayName();
                      summaryTableView.addRow(
                          typeOrOther,
                          plannedCompletionTypes.getNumberOfWellsWithPlannedCompletionType());
                    }
                ));

    return SummaryCard.tableFixedWidthSummaryCardWithHeading(PLANNED_COMPLETION_TYPE, summaryTableView);
  }

  public SummaryCard getArtificialLiftMechanismsSummaryCard(SubmissionDetail submissionDetail) {
    var summaryTableView = SummaryTableView.newWithHeading("", NUMBER_OF_WELLS);
    plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail).ifPresent(
        plannedSanctionedWells ->
            artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
                .stream()
                .sorted(Comparator.comparingInt(artificialLiftMechanisms ->
                    artificialLiftMechanisms.getArtificialLiftMechanism().getDisplayOrder()))
                .forEach(
                    artificialLiftMechanisms -> {
                      var typeOrOther = artificialLiftMechanisms.getArtificialLiftMechanism() == ArtificialLiftMechanism.OTHER
                          ? artificialLiftMechanisms.getOtherArtificialLiftMechanism()
                          : artificialLiftMechanisms.getArtificialLiftMechanism().getDisplayName();
                      summaryTableView.addRow(
                          typeOrOther,
                          artificialLiftMechanisms.getNumberOfWellsWithArtificialLiftMechanism());
                    }
                ));

    return SummaryCard.tableFixedWidthSummaryCardWithHeading(ARTIFICIAL_LIFT_MECHANISMS, summaryTableView);
  }

  private String getExpectedFaultsAsString(PlannedSanctionedWells plannedSanctionedWells) {
    return pswFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
            .stream()
            .map(pswFaults -> pswFaults.getExpectedFault().getDisplayName())
            .collect(Collectors.joining(", "));
  }

  private String getFormattedTotalCapex(PlannedSanctionedWells plannedSanctionedWells) {
    var totalCapex = "";
    if (plannedSanctionedWells.getTotalCapex() != null) {
      totalCapex = StringUtil.formatMoneyInMillions(plannedSanctionedWells.getTotalCapex());
    }
    return totalCapex;
  }

  private Integer getNumberOfWells(Boolean hasWells, BigDecimal numberOfWells) {
    return BooleanUtils.isTrue(hasWells)
        ? numberOfWells.intValue()
        : 0;
  }
}
