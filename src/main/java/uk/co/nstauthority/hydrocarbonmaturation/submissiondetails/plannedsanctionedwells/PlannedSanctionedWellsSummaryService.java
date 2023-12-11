package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.ArrayList;
import java.util.Comparator;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class PlannedSanctionedWellsSummaryService {
  public static final String PLANNED_SANCTIONED_WELLS = "Planned sanctioned wells";
  public static final String PLANNED_SANCTIONED_WELLS_WELL_TYPES = "Planned sanctioned wells - number of wells";
  public static final String HOLE_COMPLETION_TYPES = "Planned sanctioned wells - hole completion types";
  public static final String PLANNED_COMPLETION_TYPE = "Planned sanctioned wells - planned completion types";
  public static final String ARTIFICIAL_LIFT_MECHANISMS = "Planned sanctioned wells - artificial lift mechanisms";
  public static final String NUMBER_OF_WELLS = "Number of wells";

  public static final String TOTAL_CAPEX = "Total CAPEX for all planned sanctioned wells";
  public static final String TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS = "Total number of planned sanctioned wells";
  public static final String NUMBER_OF_INJECTORS = "Number of injectors";
  public static final String NUMBER_OF_PRODUCERS = "Number of producers";
  public static final String NUMBER_OF_MONITORS = "Number of monitors";
  public static final String ANY_HORIZONTAL_WELLS = "Are there any horizontal wells?";
  public static final String NUMBER_OF_HORIZONTAL_WELLS = "Horizontal wells";
  public static final String NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD = "Horizontal wells with extended reach drilling";
  public static final String ANY_HIGH_PRESSURE_TEMPERATURE_WELLS = "Are there any high pressure or high temperature wells?";
  public static final String NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS = "High pressure or high temperature wells";
  public static final String ANY_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW = "Are any wells within a narrow or tight pore " +
      "pressure fracture gradient (PPFG) window?";
  public static final String NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW = "Wells within a narrow or tight pore " +
      "pressure fracture gradient (PPFG) window";
  public static final String IS_MANAGED_PRESSURE_DRILLING_UTILIZED = "Is managed pressure drilling (MPD) utilised?";
  public static final String ANY_WELLBORE_STABILITY_CHALLENGES = "Are there wellbore stability challenges with any of the " +
      "wells?";
  public static final String NUMBER_OF_WELLBORE_STABILITY_CHALLENGES = "Wells with wellbore stability challenges";
  public static final String STABILITY_CHALLENGES_INFORMATION = "Provide information on stability challenges";
  public static final String ANY_WELLS_EXPECTED_TO_CROSS_FAULTS = "Are any of the wells expected to cross any faults?";
  public static final String NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS = "Wells expected to cross a fault";
  public static final String EXPECTED_FAULTS = "Where are faults expected?";
  public static final String ANY_EXPECTED_LOSSES_FROM_DRILLING = "Do you expect to incur losses in drilling these wells?";
  public static final String WHY_LOSSES_EXPECTED = "Why are losses expected?";
  public static final String ANY_EXPECTED_DRILLING_CHALLENGES = "Do you expect to encounter drilling challenges?";
  public static final String DRILLING_CHALLENGES_MITIGATIONS = "What mitigations do you have in place?";
  public static final String STIMULATION_TYPE_REQUIRED = "What type of well stimulation is required?";
  public static final String NUMBER_OF_WELLS_REQUIRING_STIMULATION = "Wells requiring stimulation";
  public static final String IS_EXPECTING_FORMATION_DAMAGE = "Do you envisage any formation damage?";
  public static final String EXPECTED_FORMATION_DAMAGE = "Describe the type of formation damage expected";
  public static final String ANY_HEAVY_OIL = "Is there heavy oil with an API below 20 degrees?";
  public static final String ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED = "Are specialised drilling equipment or services" +
      " required?";
  public static final String SPECIALISED_DRILLING_EQUIPMENT_REQUIRED = "What specialised drilling equipment or services" +
      " are required?";

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


          summaryDataView.addKeyValue(ANY_HORIZONTAL_WELLS,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHorizontalWells()));

          summaryDataView.addKeyValue(ANY_HIGH_PRESSURE_TEMPERATURE_WELLS,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHighPressureTemperatureWells()));

          summaryDataView.addKeyValue(ANY_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow())) {
            summaryDataView.addKeyValue(IS_MANAGED_PRESSURE_DRILLING_UTILIZED,
                plannedSanctionedWells.getIsManagedPressureDrillingUtilized());
          }

          summaryDataView.addKeyValue(ANY_WELLBORE_STABILITY_CHALLENGES,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellboreStabilityChallenges()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellboreStabilityChallenges())) {
            summaryDataView.addKeyValue(STABILITY_CHALLENGES_INFORMATION,
                plannedSanctionedWells.getStabilityChallengesInformation());
          }

          summaryDataView.addKeyValue(ANY_WELLS_EXPECTED_TO_CROSS_FAULTS,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellsExpectedToCrossFaults()));
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
          summaryDataView.addKeyValue(STIMULATION_TYPE_REQUIRED,
              plannedSanctionedWells.getStimulationTypeRequired().getDisplayName());

          summaryDataView.addKeyValue(IS_EXPECTING_FORMATION_DAMAGE,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getIsExpectingFormationDamage()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getIsExpectingFormationDamage())) {
            summaryDataView.addKeyValue(EXPECTED_FORMATION_DAMAGE,
                plannedSanctionedWells.getExpectedFormationDamage());
          }

          summaryDataView.addKeyValue(ANY_HEAVY_OIL, plannedSanctionedWells.getAnyHeavyOil());

          summaryDataView.addKeyValue(ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
              BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnySpecialisedDrillingEquipmentRequired()));
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnySpecialisedDrillingEquipmentRequired())) {
            summaryDataView.addKeyValue(SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
                plannedSanctionedWells.getSpecialisedDrillingEquipmentRequired());
          }
        },
        () -> {
          summaryDataView.addKeyValue(TOTAL_CAPEX, "");
          summaryDataView.addKeyValue(TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS, "");
          summaryDataView.addKeyValue(NUMBER_OF_INJECTORS, "");
          summaryDataView.addKeyValue(NUMBER_OF_PRODUCERS, "");
          summaryDataView.addKeyValue(NUMBER_OF_MONITORS, "");
          summaryDataView.addKeyValue(ANY_HORIZONTAL_WELLS, "");
          summaryDataView.addKeyValue(ANY_HIGH_PRESSURE_TEMPERATURE_WELLS, "");
          summaryDataView.addKeyValue(ANY_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW, "");
          summaryDataView.addKeyValue(ANY_WELLBORE_STABILITY_CHALLENGES, "");
          summaryDataView.addKeyValue(EXPECTED_FAULTS, "");
          summaryDataView.addKeyValue(ANY_WELLS_EXPECTED_TO_CROSS_FAULTS, "");
          summaryDataView.addKeyValue(ANY_EXPECTED_LOSSES_FROM_DRILLING, "");
          summaryDataView.addKeyValue(ANY_EXPECTED_DRILLING_CHALLENGES, "");
          summaryDataView.addKeyValue(STIMULATION_TYPE_REQUIRED, "");
          summaryDataView.addKeyValue(IS_EXPECTING_FORMATION_DAMAGE, "");
          summaryDataView.addKeyValue(ANY_HEAVY_OIL, "");
          summaryDataView.addKeyValue(ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED, "");
        });

    return SummaryCard.simpleSummaryCardWithHeading(PLANNED_SANCTIONED_WELLS, summaryDataView);
  }

  public SummaryCard getWellTypesSummaryCard(SubmissionDetail submissionDetail) {
    var summaryTableView = SummaryTableView.newWithHeading("", NUMBER_OF_WELLS);
    plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail).ifPresent(
        plannedSanctionedWells -> {
          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyHorizontalWells())) {
            summaryTableView.addRow(NUMBER_OF_HORIZONTAL_WELLS,
                plannedSanctionedWells.getNumberOfHorizontalWells());
            summaryTableView.addRow(NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD,
                plannedSanctionedWells.getNumberOfHorizontalWellsWithErd());
          }

          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyHighPressureTemperatureWells())) {
            summaryTableView.addRow(NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS,
                plannedSanctionedWells.getNumberOfHighPressureTemperatureWells());
          }

          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow())) {
            summaryTableView.addRow(NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
                plannedSanctionedWells.getNumberOfWellsWithinNarrowOrTightPpfgWindow());
          }

          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellboreStabilityChallenges())) {
            summaryTableView.addRow(NUMBER_OF_WELLBORE_STABILITY_CHALLENGES,
                plannedSanctionedWells.getNumberOfWellboreStabilityChallenges());
          }

          if (plannedSanctionedWells.getStimulationTypeRequired() != null) {
            summaryTableView.addRow(NUMBER_OF_WELLS_REQUIRING_STIMULATION,
                plannedSanctionedWells.getNumberOfWellsRequiringStimulation());
          }

          if (BooleanUtils.isTrue(plannedSanctionedWells.getAnyWellsExpectedToCrossFaults())) {
            summaryTableView.addRow(NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS,
                plannedSanctionedWells.getNumberOfWellsExpectedToCrossFaults());
          }
        });

    return SummaryCard.tableFixedWidthSummaryCardWithHeading(PLANNED_SANCTIONED_WELLS_WELL_TYPES, summaryTableView);
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
    return String.join(
        ", ",
        pswFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
            .stream().map(pswFaults -> pswFaults.getExpectedFault().getDisplayName())
            .toList()
    );
  }

  private String getFormattedTotalCapex(PlannedSanctionedWells plannedSanctionedWells) {
    var totalCapex = "";
    if (plannedSanctionedWells.getTotalCapex() != null) {
      totalCapex = "£%s million".formatted(plannedSanctionedWells.getTotalCapex());
    }
    return totalCapex;
  }
}
