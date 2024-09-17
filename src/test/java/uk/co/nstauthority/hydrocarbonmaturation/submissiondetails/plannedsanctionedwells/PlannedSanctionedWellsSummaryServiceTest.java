package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.assertj.core.groups.Tuple;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@ExtendWith(MockitoExtension.class)
class PlannedSanctionedWellsSummaryServiceTest {
  @Mock
  private PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  @Mock
  private PswFaultRepository pswFaultRepository;
  @Mock
  private HoleCompletionTypesRepository holeCompletionTypesRepository;
  @Mock
  private PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  @Mock
  private ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;
  @InjectMocks
  private PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService;

  private SubmissionDetail submissionDetail;

  private PlannedSanctionedWells plannedSanctionedWells;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    plannedSanctionedWells = getPlannedSanctionedWells_withNoWellNumbers();
  }

  @Test
  void getPlannedSanctionedWellsCard_noExisting() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_SANCTIONED_WELLS
        );

    var summaryDataView = ((SummaryDataView) summaryCard.summaryData()).keyValues();
    assertThat(summaryDataView)
        .extracting(SummaryKeyValue::key)
        .containsExactly(
            PlannedSanctionedWellsSummaryService.TOTAL_CAPEX,
            PlannedSanctionedWellsSummaryService.TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_INJECTORS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_PRODUCERS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_MONITORS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
            PlannedSanctionedWellsSummaryService.IS_MANAGED_PRESSURE_DRILLING_UTILIZED,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLBORE_STABILITY_CHALLENGES,
            PlannedSanctionedWellsSummaryService.STABILITY_CHALLENGES_INFORMATION,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS,
            PlannedSanctionedWellsSummaryService.EXPECTED_FAULTS,
            PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
            PlannedSanctionedWellsSummaryService.WHY_LOSSES_EXPECTED,
            PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
            PlannedSanctionedWellsSummaryService.DRILLING_CHALLENGES_MITIGATIONS,
            PlannedSanctionedWellsSummaryService.STIMULATION_TYPE_REQUIRED,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_REQUIRING_STIMULATION,
            PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
            PlannedSanctionedWellsSummaryService.EXPECTED_FORMATION_DAMAGE,
            PlannedSanctionedWellsSummaryService.ANY_HEAVY_OIL,
            PlannedSanctionedWellsSummaryService.ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
            PlannedSanctionedWellsSummaryService.SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_MULTILATERAL_WELLS
        );

    assertThat(summaryDataView)
        .extracting(SummaryKeyValue::value)
        .containsOnly("");
  }

  @Test
  void getPlannedSanctionedWellsCard_existingData_allSetNo() {
    var plannedSanctionedWell = getPlannedSanctionedWells_withNoWellNumbers();

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWell));

    var summaryCard = plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_SANCTIONED_WELLS
        );

    var summaryDataView = ((SummaryDataView) summaryCard.summaryData()).keyValues();
    assertThat(summaryDataView)
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .containsExactly(
            Tuple.tuple(PlannedSanctionedWellsSummaryService.TOTAL_CAPEX,
                "£%s million".formatted(plannedSanctionedWell.getTotalCapex())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS,
                plannedSanctionedWell.getNumberOfPlannedSanctionedWells().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_INJECTORS,
                plannedSanctionedWell.getNumberOfInjectors().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_PRODUCERS,
                plannedSanctionedWell.getNumberOfProducers().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_MONITORS,
                plannedSanctionedWell.getNumberOfMonitors().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS, "0"),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD, "0"),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS, "0"),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW, "0"),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLBORE_STABILITY_CHALLENGES, "0"),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS, "0"),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsExpectingFormationDamage())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsExpectingFormationDamage())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.STIMULATION_TYPE_REQUIRED,
                plannedSanctionedWell.getStimulationTypeRequired().getDisplayName()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsExpectingFormationDamage())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HEAVY_OIL,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyHeavyOil())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnySpecialisedDrillingEquipmentRequired())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_MULTILATERAL_WELLS, "0")
        );
  }

  @Test
  void getPlannedSanctionedWellsCard_existingData_allSetYes() {
    var plannedSanctionedWell = getPlannedSanctionedWells_withWellNumbers();

    var faultOne = new PswFaults();
    faultOne.setExpectedFault(Fault.OVERBURDEN);

    var faultTwo = new PswFaults();
    faultTwo.setExpectedFault(Fault.RESERVOIR);

    var expectedFaultsAsString = "%s, %s".formatted(Fault.OVERBURDEN.getDisplayName(), Fault.RESERVOIR.getDisplayName());

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWell));
    when(pswFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWell))
        .thenReturn(List.of(faultOne, faultTwo));

    var summaryCard = plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_SANCTIONED_WELLS
        );

    var summaryDataView = ((SummaryDataView) summaryCard.summaryData()).keyValues();
    assertThat(summaryDataView)
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .containsExactly(
            Tuple.tuple(PlannedSanctionedWellsSummaryService.TOTAL_CAPEX,
                "£%s million".formatted(plannedSanctionedWell.getTotalCapex())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS,
                plannedSanctionedWell.getNumberOfPlannedSanctionedWells().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_INJECTORS,
                plannedSanctionedWell.getNumberOfInjectors().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_PRODUCERS,
                plannedSanctionedWell.getNumberOfProducers().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_MONITORS,
                plannedSanctionedWell.getNumberOfMonitors().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS,
                plannedSanctionedWell.getNumberOfHorizontalWells().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD,
                plannedSanctionedWell.getNumberOfHorizontalWellsWithErd().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS,
                plannedSanctionedWell.getNumberOfHighPressureTemperatureWells().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
                plannedSanctionedWell.getNumberOfWellsWithinNarrowOrTightPpfgWindow().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.IS_MANAGED_PRESSURE_DRILLING_UTILIZED,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsManagedPressureDrillingUtilized())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLBORE_STABILITY_CHALLENGES,
                plannedSanctionedWell.getNumberOfWellboreStabilityChallenges().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.STABILITY_CHALLENGES_INFORMATION,
                plannedSanctionedWell.getStabilityChallengesInformation()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS,
                plannedSanctionedWell.getNumberOfWellsExpectedToCrossFaults().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.EXPECTED_FAULTS, expectedFaultsAsString),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsExpectingFormationDamage())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.WHY_LOSSES_EXPECTED,
                plannedSanctionedWell.getWhyLossesExpected()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsExpectingFormationDamage())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.DRILLING_CHALLENGES_MITIGATIONS,
                plannedSanctionedWell.getDrillingChallengesMitigations()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.STIMULATION_TYPE_REQUIRED,
                plannedSanctionedWell.getStimulationTypeRequired().getDisplayName()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsExpectingFormationDamage())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.EXPECTED_FORMATION_DAMAGE,
                plannedSanctionedWell.getExpectedFormationDamage()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HEAVY_OIL,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyHeavyOil())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnySpecialisedDrillingEquipmentRequired())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
                plannedSanctionedWell.getSpecialisedDrillingEquipmentRequired()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_MULTILATERAL_WELLS,
                plannedSanctionedWell.getNumberOfMultilateralWells().toString())
        );
  }

  @Test
  void getHoleCompletionTypesSummaryCard_noExistingDetail_assertCardProperties() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = plannedSanctionedWellsSummaryService.getHoleCompletionTypesSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.HOLE_COMPLETION_TYPES
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of(
        new SummaryTableValue(""),
        new SummaryTableValue(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    ));
    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .containsExactly(expectedTableHeaderRow);
  }

  @Test
  void getHoleCompletionTypesSummaryCard_existingDetail_assertCardProperties() {
    var holeCompletionType1 = new HoleCompletionTypes();
    holeCompletionType1.setHoleCompletionType(HoleCompletionType.OPENED_HOLE);
    holeCompletionType1.setNumberOfWellsWithHoleCompletionType(BigDecimal.valueOf(5));

    var holeCompletionType2 = new HoleCompletionTypes();
    holeCompletionType2.setHoleCompletionType(HoleCompletionType.CASED_HOLE);
    holeCompletionType2.setNumberOfWellsWithHoleCompletionType(BigDecimal.valueOf(2));

    var holeCompletionTypes = List.of(holeCompletionType1, holeCompletionType2);

    when(holeCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(holeCompletionTypes);

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var summaryCard = plannedSanctionedWellsSummaryService.getHoleCompletionTypesSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.HOLE_COMPLETION_TYPES
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of(
        new SummaryTableValue(""),
        new SummaryTableValue(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    ));

    var expectedRow1 = new SummaryTableRow(List.of(
        new SummaryTableValue(HoleCompletionType.OPENED_HOLE.getDisplayName()),
        new SummaryTableValue(holeCompletionType1.getNumberOfWellsWithHoleCompletionType().toString())
    ));
    var expectedRow2 = new SummaryTableRow(List.of(
        new SummaryTableValue(HoleCompletionType.CASED_HOLE.getDisplayName()),
        new SummaryTableValue(holeCompletionType2.getNumberOfWellsWithHoleCompletionType().toString())
    ));

    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .containsExactly(expectedTableHeaderRow, expectedRow1, expectedRow2);
  }

  @Test
  void getPlannedCompletionTypesSummaryCard_noExistingDetail_assertCardProperties() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = plannedSanctionedWellsSummaryService.getPlannedCompletionTypesSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_COMPLETION_TYPE
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of(
        new SummaryTableValue(""),
        new SummaryTableValue(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    ));
    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .containsExactly(expectedTableHeaderRow);
  }

  @Test
  void getPlannedCompletionTypesSummaryCard_existingDetail_assertCardProperties() {
    var plannedCompletionType1 = new PlannedCompletionTypes();
    plannedCompletionType1.setPlannedCompletionType(PlannedCompletionType.GRAVELPACK);
    plannedCompletionType1.setNumberOfWellsWithPlannedCompletionType(BigDecimal.valueOf(5));

    var plannedCompletionType2 = new PlannedCompletionTypes();
    plannedCompletionType2.setPlannedCompletionType(PlannedCompletionType.CEMENTED_LINER);
    plannedCompletionType2.setNumberOfWellsWithPlannedCompletionType(BigDecimal.valueOf(2));

    var plannedCompletionTypes = List.of(plannedCompletionType1, plannedCompletionType2);

    when(plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(plannedCompletionTypes);

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var summaryCard = plannedSanctionedWellsSummaryService.getPlannedCompletionTypesSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_COMPLETION_TYPE
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of(
        new SummaryTableValue(""),
        new SummaryTableValue(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    ));

    var expectedRow1 = new SummaryTableRow(List.of(
        new SummaryTableValue(plannedCompletionType1.getPlannedCompletionType().getDisplayName()),
        new SummaryTableValue(plannedCompletionType1.getNumberOfWellsWithPlannedCompletionType().toString())
    ));
    var expectedRow2 = new SummaryTableRow(List.of(
        new SummaryTableValue(plannedCompletionType2.getPlannedCompletionType().getDisplayName()),
        new SummaryTableValue(plannedCompletionType2.getNumberOfWellsWithPlannedCompletionType().toString())
    ));

    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .containsExactly(expectedTableHeaderRow, expectedRow1, expectedRow2);
  }

  @Test
  void getArtificialLiftMechanismsSummaryCard_noExistingDetail_assertCardProperties() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = plannedSanctionedWellsSummaryService.getArtificialLiftMechanismsSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.ARTIFICIAL_LIFT_MECHANISMS
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of(
        new SummaryTableValue(""),
        new SummaryTableValue(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    ));

    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .containsExactly(expectedTableHeaderRow);
  }

  @Test
  void getArtificialLiftMechanismsSummaryCard_existingDetail_assertCardProperties() {
    var artificialLiftMechansism1 = new ArtificialLiftMechanisms();
    artificialLiftMechansism1.setArtificialLiftMechanism(ArtificialLiftMechanism.GAS_LIFT);
    artificialLiftMechansism1.setNumberOfWellsWithArtificialLiftMechanism(BigDecimal.valueOf(5));

    var artificialLiftMechansism2 = new ArtificialLiftMechanisms();
    artificialLiftMechansism2.setArtificialLiftMechanism(ArtificialLiftMechanism.OTHER);
    artificialLiftMechansism2.setOtherArtificialLiftMechanism("Other lift mechanism");
    artificialLiftMechansism2.setNumberOfWellsWithArtificialLiftMechanism(BigDecimal.valueOf(2));

    var artificialLiftMechansisms = List.of(artificialLiftMechansism1, artificialLiftMechansism2);

    when(artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(artificialLiftMechansisms);

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var summaryCard = plannedSanctionedWellsSummaryService.getArtificialLiftMechanismsSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.ARTIFICIAL_LIFT_MECHANISMS
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of(
        new SummaryTableValue(""),
        new SummaryTableValue(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    ));

    var expectedRow1 = new SummaryTableRow(List.of(
        new SummaryTableValue(artificialLiftMechansism1.getArtificialLiftMechanism().getDisplayName()),
        new SummaryTableValue(artificialLiftMechansism1.getNumberOfWellsWithArtificialLiftMechanism().toString())
    ));
    var expectedRow2 = new SummaryTableRow(List.of(
        new SummaryTableValue(artificialLiftMechansism2.getOtherArtificialLiftMechanism()),
        new SummaryTableValue(artificialLiftMechansism2.getNumberOfWellsWithArtificialLiftMechanism().toString())
    ));

    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .containsExactly(expectedTableHeaderRow, expectedRow1, expectedRow2);
  }

  @NotNull
  private static PlannedSanctionedWells getPlannedSanctionedWells_withNoWellNumbers() {
    var plannedSanctionedWell = new PlannedSanctionedWells();
    plannedSanctionedWell.setTotalCapex(BigDecimal.valueOf(10));
    plannedSanctionedWell.setNumberOfPlannedSanctionedWells(BigDecimal.valueOf(10));
    plannedSanctionedWell.setNumberOfInjectors(BigDecimal.valueOf(5));
    plannedSanctionedWell.setNumberOfProducers(BigDecimal.valueOf(3));
    plannedSanctionedWell.setNumberOfMonitors(BigDecimal.valueOf(2));
    plannedSanctionedWell.setAnyHorizontalWells(false);
    plannedSanctionedWell.setAnyHighPressureTemperatureWells(false);
    plannedSanctionedWell.setAnyWellsWithinNarrowOrTightPpfgWindow(false);
    plannedSanctionedWell.setAnyWellboreStabilityChallenges(false);
    plannedSanctionedWell.setAnyWellsExpectedToCrossFaults(false);
    plannedSanctionedWell.setAnyExpectedLossesFromDrilling(false);
    plannedSanctionedWell.setAnyExpectedDrillingChallenges(false);
    plannedSanctionedWell.setStimulationTypeRequired(StimulationType.NO_WELL_STIMULATION);
    plannedSanctionedWell.setExpectingFormationDamage(false);
    plannedSanctionedWell.setAnyHeavyOil(false);
    plannedSanctionedWell.setAnySpecialisedDrillingEquipmentRequired(false);
    plannedSanctionedWell.setAnyMultilateralWells(false);
    return plannedSanctionedWell;
  }

  private static PlannedSanctionedWells getPlannedSanctionedWells_withWellNumbers() {
    var plannedSanctionedWell = new PlannedSanctionedWells();
    plannedSanctionedWell.setTotalCapex(BigDecimal.valueOf(10));
    plannedSanctionedWell.setNumberOfPlannedSanctionedWells(BigDecimal.valueOf(10));
    plannedSanctionedWell.setNumberOfInjectors(BigDecimal.valueOf(5));
    plannedSanctionedWell.setNumberOfProducers(BigDecimal.valueOf(3));
    plannedSanctionedWell.setNumberOfMonitors(BigDecimal.valueOf(2));

    plannedSanctionedWell.setAnyHorizontalWells(true);
    plannedSanctionedWell.setNumberOfHorizontalWells(BigDecimal.valueOf(1));
    plannedSanctionedWell.setNumberOfHorizontalWellsWithErd(BigDecimal.valueOf(1));

    plannedSanctionedWell.setAnyHighPressureTemperatureWells(true);
    plannedSanctionedWell.setNumberOfHighPressureTemperatureWells(BigDecimal.valueOf(2));

    plannedSanctionedWell.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    plannedSanctionedWell.setNumberOfWellsWithinNarrowOrTightPpfgWindow(BigDecimal.valueOf(1));
    plannedSanctionedWell.setIsManagedPressureDrillingUtilized(true);

    plannedSanctionedWell.setAnyWellboreStabilityChallenges(true);
    plannedSanctionedWell.setNumberOfWellboreStabilityChallenges(BigDecimal.valueOf(5));
    plannedSanctionedWell.setStabilityChallengesInformation("stability challenges information");

    plannedSanctionedWell.setAnyWellsExpectedToCrossFaults(true);
    plannedSanctionedWell.setNumberOfWellsExpectedToCrossFaults(BigDecimal.valueOf(6));

    plannedSanctionedWell.setAnyExpectedLossesFromDrilling(true);
    plannedSanctionedWell.setWhyLossesExpected("just because");

    plannedSanctionedWell.setAnyExpectedDrillingChallenges(true);
    plannedSanctionedWell.setDrillingChallengesMitigations("mitigate them");

    plannedSanctionedWell.setStimulationTypeRequired(StimulationType.NO_WELL_STIMULATION);
    plannedSanctionedWell.setNumberOfWellsRequiringStimulation(BigDecimal.valueOf(4));

    plannedSanctionedWell.setExpectingFormationDamage(true);
    plannedSanctionedWell.setExpectedFormationDamage("so much damage!");

    plannedSanctionedWell.setAnyHeavyOil(true);

    plannedSanctionedWell.setAnySpecialisedDrillingEquipmentRequired(true);
    plannedSanctionedWell.setSpecialisedDrillingEquipmentRequired("special 💫");

    plannedSanctionedWell.setAnyMultilateralWells(true);
    plannedSanctionedWell.setNumberOfMultilateralWells(BigDecimal.ONE);

    return plannedSanctionedWell;
  }
}