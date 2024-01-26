package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
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
            PlannedSanctionedWellsSummaryService.ANY_HORIZONTAL_WELLS,
            PlannedSanctionedWellsSummaryService.ANY_HIGH_PRESSURE_TEMPERATURE_WELLS,
            PlannedSanctionedWellsSummaryService.ANY_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
            PlannedSanctionedWellsSummaryService.ANY_WELLBORE_STABILITY_CHALLENGES,
            PlannedSanctionedWellsSummaryService.EXPECTED_FAULTS,
            PlannedSanctionedWellsSummaryService.ANY_WELLS_EXPECTED_TO_CROSS_FAULTS,
            PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
            PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
            PlannedSanctionedWellsSummaryService.STIMULATION_TYPE_REQUIRED,
            PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
            PlannedSanctionedWellsSummaryService.ANY_HEAVY_OIL,
            PlannedSanctionedWellsSummaryService.ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
            PlannedSanctionedWellsSummaryService.ANY_MULTILATERAL_WELLS
        );

    assertThat(summaryDataView)
        .extracting(SummaryKeyValue::value)
        .containsOnly("");
  }

  @Test
  void getWellTypesSummaryCard_noExisting() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = plannedSanctionedWellsSummaryService.getWellTypesSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_SANCTIONED_WELLS_WELL_TYPES
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS));
    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .containsExactly(expectedTableHeaderRow);
  }

  @Test
  void getWellTypesSummaryCard_existing_assertCardProperties() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var summaryCard = plannedSanctionedWellsSummaryService.getWellTypesSummaryCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_SANCTIONED_WELLS_WELL_TYPES
        );

    var expectedTableHeaderRow = new SummaryTableRow(List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS));
    var summaryTableView = ((SummaryTableView) summaryCard.summaryData());
    assertThat(summaryTableView.tableRows())
        .contains(expectedTableHeaderRow);
  }

  @Test
  void getPlannedSanctionedWellsCard_existingData_allTextFieldsSetNo() {
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
        .containsExactly(Tuple.tuple(PlannedSanctionedWellsSummaryService.TOTAL_CAPEX,
                "£%s million".formatted(plannedSanctionedWell.getTotalCapex())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS,
                plannedSanctionedWell.getNumberOfPlannedSanctionedWells().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_INJECTORS,
                plannedSanctionedWell.getNumberOfInjectors().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_PRODUCERS,
                plannedSanctionedWell.getNumberOfProducers().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_MONITORS,
                plannedSanctionedWell.getNumberOfMonitors().toString()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HORIZONTAL_WELLS,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyHorizontalWells())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HIGH_PRESSURE_TEMPERATURE_WELLS,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyHighPressureTemperatureWells())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyWellsWithinNarrowOrTightPpfgWindow())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLBORE_STABILITY_CHALLENGES,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyWellboreStabilityChallenges())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLS_EXPECTED_TO_CROSS_FAULTS,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyWellsExpectedToCrossFaults())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyExpectedLossesFromDrilling())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyExpectedDrillingChallenges())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.STIMULATION_TYPE_REQUIRED,
                plannedSanctionedWell.getStimulationTypeRequired().getDisplayName()),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getIsExpectingFormationDamage())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HEAVY_OIL,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyHeavyOil())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnySpecialisedDrillingEquipmentRequired())),
            Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_MULTILATERAL_WELLS,
                BooleanUtil.yesNoFromBoolean(plannedSanctionedWell.getAnyMultilateralWells()))
        );
  }

  private void testSummary_hasTextFields_andHasWellTypes_andDoesNotHaveKeys(
      List<Tuple> expectedTextFields, List<Tuple> expectedWellTypes, List<String> keysNotInSummaries) {

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    // check text summary card
    var textFieldsSummaryCard = plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail);

    var textFieldsSummaryDataView = ((SummaryDataView) textFieldsSummaryCard.summaryData()).keyValues();
    assertThat(textFieldsSummaryDataView)
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .containsAll(expectedTextFields);

    if (!keysNotInSummaries.isEmpty()) {
      assertThat(textFieldsSummaryDataView)
          .extracting(SummaryKeyValue::key)
          .doesNotContainAnyElementsOf(keysNotInSummaries);
    }

    var expectedRows = expectedWellTypes.stream()
        .map(Tuple::toList)
        .map(objects -> objects.stream()
            .map(Object::toString)
            .toList())
        .map(SummaryTableRow::new)
        .toList();

    // check well types summary card
    var wellTypesSummaryCard = plannedSanctionedWellsSummaryService.getWellTypesSummaryCard(submissionDetail);
    var wellTypesSummaryTableView = ((SummaryTableView) wellTypesSummaryCard.summaryData());

    if (!expectedRows.isEmpty()) {
      assertThat(wellTypesSummaryTableView.tableRows())
          .containsAnyElementsOf(expectedRows);
    }

    if (!keysNotInSummaries.isEmpty()) {
      assertThat(wellTypesSummaryTableView.tableRows())
          .extracting(SummaryTableRow::rowValues)
          .doesNotContainAnyElementsOf(Collections.singleton(keysNotInSummaries));
    }

  }

  private void testSummary_hasTextFields_andHasWellTypes(List<Tuple> expectedTextFields, List<Tuple> expectedWellTypes) {
    testSummary_hasTextFields_andHasWellTypes_andDoesNotHaveKeys(
        expectedTextFields, expectedWellTypes, Collections.emptyList());
  }

  private void testSummary_hasTextFields_andDoesNotHaveKeys(
      List<Tuple> expectedTextFields, List<String> notPresentInSummaries) {
    testSummary_hasTextFields_andHasWellTypes_andDoesNotHaveKeys(
        expectedTextFields, Collections.emptyList(), notPresentInSummaries);
  }

  private void testSummary_hasTextFields(List<Tuple> expectedTextFields) {
    testSummary_hasTextFields_andHasWellTypes_andDoesNotHaveKeys(
        expectedTextFields, Collections.emptyList(), Collections.emptyList());
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_horizontalWellsNo() {
    plannedSanctionedWells.setAnyHorizontalWells(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HORIZONTAL_WELLS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHorizontalWells()))
    );

    var notPresentKeys = List.of(
        PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS,
        PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD
    );

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_horizontalWellsYes() {
    plannedSanctionedWells.setAnyHorizontalWells(true);
    plannedSanctionedWells.setNumberOfHorizontalWells(BigDecimal.valueOf(10));
    plannedSanctionedWells.setNumberOfHorizontalWellsWithErd(BigDecimal.valueOf(0));

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HORIZONTAL_WELLS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHorizontalWells()))
    );

    var expectedWellTypes = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS,
            plannedSanctionedWells.getNumberOfHorizontalWells().toString()),
        Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD,
            plannedSanctionedWells.getNumberOfHorizontalWellsWithErd().toString())
    );

    testSummary_hasTextFields_andHasWellTypes(expectedTextFields, expectedWellTypes);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_highPressureWellsYes() {
    plannedSanctionedWells.setAnyHighPressureTemperatureWells(true);
    plannedSanctionedWells.setNumberOfHighPressureTemperatureWells(BigDecimal.valueOf(10));

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HIGH_PRESSURE_TEMPERATURE_WELLS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHighPressureTemperatureWells()))
    );

    var expectedWellTypes = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS,
            plannedSanctionedWells.getNumberOfHighPressureTemperatureWells().toString())
    );

    testSummary_hasTextFields_andHasWellTypes(expectedTextFields, expectedWellTypes);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_highPressureWellsNo() {
    plannedSanctionedWells.setAnyHighPressureTemperatureWells(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HIGH_PRESSURE_TEMPERATURE_WELLS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHighPressureTemperatureWells()))
    );

    var notPresentKeys = List.of(
        PlannedSanctionedWellsSummaryService.NUMBER_OF_HIGH_PRESSURE_TEMPERATURE_WELLS
    );

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_WellsNarrowOrTightPpfgYes() {
    plannedSanctionedWells.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    plannedSanctionedWells.setNumberOfWellsWithinNarrowOrTightPpfgWindow(BigDecimal.valueOf(10));
    plannedSanctionedWells.setIsManagedPressureDrillingUtilized(true);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow())),
        Tuple.tuple(PlannedSanctionedWellsSummaryService.IS_MANAGED_PRESSURE_DRILLING_UTILIZED,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getIsManagedPressureDrillingUtilized()))
    );

    var expectedWellTypes = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
            plannedSanctionedWells.getNumberOfWellsWithinNarrowOrTightPpfgWindow().toString())
    );

    testSummary_hasTextFields_andHasWellTypes(expectedTextFields, expectedWellTypes);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_WellsNarrowOrTightPpfgNo() {
    plannedSanctionedWells.setAnyHorizontalWells(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HORIZONTAL_WELLS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHorizontalWells()))
        );

    var notPresentKeys = List.of(
        PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS,
        PlannedSanctionedWellsSummaryService.NUMBER_OF_HORIZONTAL_WELLS_WITH_ERD
    );

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_WellboreStabilityChallengesYes() {
    plannedSanctionedWells.setAnyWellboreStabilityChallenges(true);
    plannedSanctionedWells.setNumberOfWellboreStabilityChallenges(BigDecimal.valueOf(10));
    plannedSanctionedWells.setStabilityChallengesInformation("Information");

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLBORE_STABILITY_CHALLENGES,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellboreStabilityChallenges())),
        Tuple.tuple(PlannedSanctionedWellsSummaryService.STABILITY_CHALLENGES_INFORMATION,
            plannedSanctionedWells.getStabilityChallengesInformation())
    );

    var expectedWellTypes = List.of(
        Tuple.tuple(
          PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLBORE_STABILITY_CHALLENGES,
          plannedSanctionedWells.getNumberOfWellboreStabilityChallenges()
        )
    );

    testSummary_hasTextFields_andHasWellTypes(expectedTextFields, expectedWellTypes);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_WellboreStabilityChallengesNo() {
    plannedSanctionedWells.setAnyWellboreStabilityChallenges(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLBORE_STABILITY_CHALLENGES,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellboreStabilityChallenges()))
    );

    var notPresentKeys = List.of(
        PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLBORE_STABILITY_CHALLENGES,
        PlannedSanctionedWellsSummaryService.STABILITY_CHALLENGES_INFORMATION
    );

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_WellsExpectedToCrossFaultsYes() {
    plannedSanctionedWells.setAnyWellsExpectedToCrossFaults(true);
    plannedSanctionedWells.setNumberOfWellsExpectedToCrossFaults(BigDecimal.valueOf(10));

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLS_EXPECTED_TO_CROSS_FAULTS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellsExpectedToCrossFaults()))
    );

    var expectedWellTypes = List.of(
        Tuple.tuple(
            PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS,
            plannedSanctionedWells.getNumberOfWellsExpectedToCrossFaults()
        )
    );

    testSummary_hasTextFields_andHasWellTypes(expectedTextFields, expectedWellTypes);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_WellsExpectedToCrossFaultsNo() {
    plannedSanctionedWells.setAnyWellsExpectedToCrossFaults(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_WELLS_EXPECTED_TO_CROSS_FAULTS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyWellsExpectedToCrossFaults()))
    );

    var notPresentKeys = List.of(
        PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS_EXPECTED_TO_CROSS_FAULTS
    );

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_expectedLossesFromDrillingYes() {
    plannedSanctionedWells.setAnyExpectedLossesFromDrilling(true);
    plannedSanctionedWells.setWhyLossesExpected("The reasons to expect losses");

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyExpectedLossesFromDrilling())),
        Tuple.tuple(PlannedSanctionedWellsSummaryService.WHY_LOSSES_EXPECTED,
            plannedSanctionedWells.getWhyLossesExpected())
    );

    testSummary_hasTextFields(expectedTextFields);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_expectedLossesFromDrillingNo() {
    plannedSanctionedWells.setAnyExpectedLossesFromDrilling(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyExpectedLossesFromDrilling()))
    );

    var notPresentKeys = List.of(
        PlannedSanctionedWellsSummaryService.WHY_LOSSES_EXPECTED
    );

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_drillingChallengesYes() {
    plannedSanctionedWells.setAnyExpectedDrillingChallenges(true);
    plannedSanctionedWells.setDrillingChallengesMitigations("The mitigations");

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyExpectedDrillingChallenges())),
        Tuple.tuple(PlannedSanctionedWellsSummaryService.DRILLING_CHALLENGES_MITIGATIONS,
            plannedSanctionedWells.getDrillingChallengesMitigations())
    );

    testSummary_hasTextFields(expectedTextFields);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_drillingChallengesNo() {
    plannedSanctionedWells.setAnyExpectedDrillingChallenges(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyExpectedDrillingChallenges()))
    );

    var notPresentKeys = List.of(
        PlannedSanctionedWellsSummaryService.DRILLING_CHALLENGES_MITIGATIONS
    );

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_stimulationTypeRequiredYes() {
    plannedSanctionedWells.setStimulationTypeRequired(StimulationType.HYDRAULIC_FRACTURING);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.STIMULATION_TYPE_REQUIRED,
            plannedSanctionedWells.getStimulationTypeRequired().getDisplayName())
    );

    testSummary_hasTextFields(expectedTextFields);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_expectingFormationDamageYes() {
    plannedSanctionedWells.setExpectingFormationDamage(true);
    plannedSanctionedWells.setExpectedFormationDamage("The damage expected");

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getIsExpectingFormationDamage())),
        Tuple.tuple(PlannedSanctionedWellsSummaryService.EXPECTED_FORMATION_DAMAGE,
            plannedSanctionedWells.getExpectedFormationDamage())
    );

    testSummary_hasTextFields(expectedTextFields);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_expectingFormationDamageNo() {
    plannedSanctionedWells.setExpectingFormationDamage(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getIsExpectingFormationDamage()))
    );

    var notPresentKeys = List.of(PlannedSanctionedWellsSummaryService.EXPECTED_FORMATION_DAMAGE);

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_heavyOil() {
    plannedSanctionedWells.setAnyHeavyOil(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_HEAVY_OIL,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyHeavyOil()))
    );

    testSummary_hasTextFields(expectedTextFields);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_multilateralWellsYes() {
    plannedSanctionedWells.setAnyMultilateralWells(true);
    plannedSanctionedWells.setNumberOfMultilateralWells(BigDecimal.valueOf(5));

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_MULTILATERAL_WELLS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyMultilateralWells()))
    );

    var expectedWellTypes = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.NUMBER_OF_MULTILATERAL_WELLS,
            plannedSanctionedWells.getNumberOfMultilateralWells())
    );

    testSummary_hasTextFields_andHasWellTypes(expectedTextFields, expectedWellTypes);
  }

  @Test
  void getPlannedSanctionedWellsSummaryCards_existingData_multilateralWellsNo() {
    plannedSanctionedWells.setAnyMultilateralWells(false);

    var expectedTextFields = List.of(
        Tuple.tuple(PlannedSanctionedWellsSummaryService.ANY_MULTILATERAL_WELLS,
            BooleanUtil.yesNoFromBoolean(plannedSanctionedWells.getAnyMultilateralWells()))
    );

    var notPresentKeys = List.of(PlannedSanctionedWellsSummaryService.NUMBER_OF_MULTILATERAL_WELLS);

    testSummary_hasTextFields_andDoesNotHaveKeys(expectedTextFields, notPresentKeys);
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

    var expectedTableHeaderRow = new SummaryTableRow(List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS));
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

    var expectedTableHeaderRow = new SummaryTableRow(
        List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    );

    var expectedRow1 = new SummaryTableRow(
        List.of(HoleCompletionType.OPENED_HOLE.getDisplayName(),
            holeCompletionType1.getNumberOfWellsWithHoleCompletionType().toString()));
    var expectedRow2 = new SummaryTableRow(
        List.of(HoleCompletionType.CASED_HOLE.getDisplayName(),
            holeCompletionType2.getNumberOfWellsWithHoleCompletionType().toString())
    );

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

    var expectedTableHeaderRow = new SummaryTableRow(List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS));
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

    var expectedTableHeaderRow = new SummaryTableRow(
        List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    );

    var expectedRow1 = new SummaryTableRow(
        List.of(plannedCompletionType1.getPlannedCompletionType().getDisplayName(),
            plannedCompletionType1.getNumberOfWellsWithPlannedCompletionType().toString()));
    var expectedRow2 = new SummaryTableRow(
        List.of(plannedCompletionType2.getPlannedCompletionType().getDisplayName(),
            plannedCompletionType2.getNumberOfWellsWithPlannedCompletionType().toString())
    );

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

    var expectedTableHeaderRow = new SummaryTableRow(List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS));
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

    var expectedTableHeaderRow = new SummaryTableRow(
        List.of("", PlannedSanctionedWellsSummaryService.NUMBER_OF_WELLS)
    );

    var expectedRow1 = new SummaryTableRow(
        List.of(artificialLiftMechansism1.getArtificialLiftMechanism().getDisplayName(),
            artificialLiftMechansism1.getNumberOfWellsWithArtificialLiftMechanism().toString()));
    var expectedRow2 = new SummaryTableRow(
        List.of(artificialLiftMechansism2.getOtherArtificialLiftMechanism(),
            artificialLiftMechansism2.getNumberOfWellsWithArtificialLiftMechanism().toString())
    );

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
}