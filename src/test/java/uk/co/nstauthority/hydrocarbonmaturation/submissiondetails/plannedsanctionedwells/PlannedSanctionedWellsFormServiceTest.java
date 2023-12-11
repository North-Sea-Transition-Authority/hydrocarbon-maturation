package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class PlannedSanctionedWellsFormServiceTest {

  @Mock
  private PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  @Mock
  private PwsFaultRepository pwsFaultRepository;
  @Mock
  private HoleCompletionTypesRepository holeCompletionTypesRepository;
  @Mock
  private PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  @Mock
  private ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;
  @InjectMocks
  private PlannedSanctionedWellsFormService plannedSanctionedWellsFormService;

  private SubmissionDetail submissionDetail;
  private PlannedSanctionedWells plannedSanctionedWells;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    plannedSanctionedWells = new PlannedSanctionedWells();
  }

  @Test
  void submissionDetailToForm_noExisting() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(new PlannedSanctionedWellsForm());
  }

  @Test
  void submissionDetailToForm_existing_plannedSanctionedWellsFields() {
    plannedSanctionedWells.setTotalCapex(BigDecimal.valueOf(100));
    plannedSanctionedWells.setNumberOfPlannedSanctionedWells(BigDecimal.valueOf(10));
    plannedSanctionedWells.setNumberOfInjectors(BigDecimal.valueOf(5));
    plannedSanctionedWells.setNumberOfProducers(BigDecimal.valueOf(3));
    plannedSanctionedWells.setNumberOfMonitors(BigDecimal.valueOf(2));
    plannedSanctionedWells.setAnyHorizontalWells(true);
    plannedSanctionedWells.setNumberOfHorizontalWells(BigDecimal.valueOf(4));
    plannedSanctionedWells.setNumberOfHorizontalWellsWithErd(BigDecimal.valueOf(1));
    plannedSanctionedWells.setAnyHighPressureTemperatureWells(false);
    plannedSanctionedWells.setNumberOfHighPressureTemperatureWells(null);
    plannedSanctionedWells.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    plannedSanctionedWells.setNumberOfWellsWithinNarrowOrTightPpfgWindow(BigDecimal.valueOf(5));
    plannedSanctionedWells.setIsManagedPressureDrillingUtilized(true);
    plannedSanctionedWells.setAnyWellboreStabilityChallenges(false);
    plannedSanctionedWells.setNumberOfWellboreStabilityChallenges(null);
    plannedSanctionedWells.setStabilityChallengesInformation(null);
    plannedSanctionedWells.setAnyWellsExpectedToCrossFaults(true);
    plannedSanctionedWells.setNumberOfWellsExpectedToCrossFaults(BigDecimal.valueOf(10));
    plannedSanctionedWells.setAnyExpectedLossesFromDrilling(false);
    plannedSanctionedWells.setWhyLossesExpected(null);
    plannedSanctionedWells.setAnyExpectedDrillingChallenges(false);
    plannedSanctionedWells.setDrillingChallengesMitigations(null);
    plannedSanctionedWells.setStimulationTypeRequired(StimulationType.NO_WELL_STIMULATION);
    plannedSanctionedWells.setNumberOfWellsRequiringStimulation(null);
    plannedSanctionedWells.setExpectingFormationDamage(false);
    plannedSanctionedWells.setExpectedFormationDamage(null);
    plannedSanctionedWells.setAnyHeavyOil(true);
    plannedSanctionedWells.setAnySpecialisedDrillingEquipmentRequired(false);
    plannedSanctionedWells.setSpecialisedDrillingEquipmentRequired(null);

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.ofNullable(plannedSanctionedWells));

    // check big decimals and decimal inputs have equal values
    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail))
      .extracting(
          PlannedSanctionedWellsForm::getTotalCapex,
          PlannedSanctionedWellsForm::getTotalNumberOfPlannedSanctionedWells,
          PlannedSanctionedWellsForm::getNumberOfInjectors,
          PlannedSanctionedWellsForm::getNumberOfProducers,
          PlannedSanctionedWellsForm::getNumberOfMonitors,
          PlannedSanctionedWellsForm::getNumberOfHorizontalWells,
          PlannedSanctionedWellsForm::getNumberOfHorizontalWellsWithErd,
          PlannedSanctionedWellsForm::getNumberOfHighPressureTemperatureWells,
          PlannedSanctionedWellsForm::getNumberOfWellsWithinNarrowOrTightPpfgWindow,
          PlannedSanctionedWellsForm::getNumberOfWellboreStabilityChallenges,
          PlannedSanctionedWellsForm::getNumberOfWellsExpectedToCrossFaults
      ).usingRecursiveComparison()
        .isEqualTo(List.of(
                DecimalInput.createDecimalInput(plannedSanctionedWells.getTotalCapex()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfPlannedSanctionedWells()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfInjectors()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfProducers()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfMonitors()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfHorizontalWells()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfHorizontalWellsWithErd()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfHighPressureTemperatureWells()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellsWithinNarrowOrTightPpfgWindow()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellboreStabilityChallenges()),
                DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellsExpectedToCrossFaults())
            ));

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail))
        .extracting(
            PlannedSanctionedWellsForm::getAnyHorizontalWells,
            PlannedSanctionedWellsForm::getAnyHighPressureTemperatureWells,
            PlannedSanctionedWellsForm::getAnyWellsWithinNarrowOrTightPpfgWindow,
            PlannedSanctionedWellsForm::getIsManagedPressureDrillingUtilized,
            PlannedSanctionedWellsForm::getAnyWellboreStabilityChallenges,
            PlannedSanctionedWellsForm::getStabilityChallengesInformation,
            PlannedSanctionedWellsForm::getAnyWellsExpectedToCrossFaults,
            PlannedSanctionedWellsForm::getAnyExpectedLossesFromDrilling,
            PlannedSanctionedWellsForm::getWhyLossesExpected,
            PlannedSanctionedWellsForm::getAnyExpectedDrillingChallenges,
            PlannedSanctionedWellsForm::getDrillingChallengesMitigations,
            PlannedSanctionedWellsForm::getIsExpectingFormationDamage,
            PlannedSanctionedWellsForm::getExpectedFormationDamage,
            PlannedSanctionedWellsForm::getAnyHeavyOil,
            PlannedSanctionedWellsForm::getAnySpecialisedDrillingEquipmentRequired,
            PlannedSanctionedWellsForm::getSpecialisedDrillingEquipmentRequired
        ).containsExactly(
            plannedSanctionedWells.getAnyHorizontalWells(),
            plannedSanctionedWells.getAnyHighPressureTemperatureWells(),
            plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow(),
            plannedSanctionedWells.getIsManagedPressureDrillingUtilized(),
            plannedSanctionedWells.getAnyWellboreStabilityChallenges(),
            plannedSanctionedWells.getStabilityChallengesInformation(),
            plannedSanctionedWells.getAnyWellsExpectedToCrossFaults(),
            plannedSanctionedWells.getAnyExpectedLossesFromDrilling(),
            plannedSanctionedWells.getWhyLossesExpected(),
            plannedSanctionedWells.getAnyExpectedDrillingChallenges(),
            plannedSanctionedWells.getDrillingChallengesMitigations(),
            plannedSanctionedWells.getIsExpectingFormationDamage(),
            plannedSanctionedWells.getExpectedFormationDamage(),
            plannedSanctionedWells.getAnyHeavyOil(),
            plannedSanctionedWells.getAnySpecialisedDrillingEquipmentRequired(),
            plannedSanctionedWells.getSpecialisedDrillingEquipmentRequired()
        );
  }

  @Test
  void submissionDetailToForm_noExisting_pwsFaults() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));
    when(pwsFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of());

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getExpectedFaults())
        .isEmpty();
  }

  @Test
  void submissionDetailToForm_existing_pwsFaults() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var fault1 = new PswFaults();
    fault1.setExpectedFault(Fault.RESERVOIR);
    var fault2 = new PswFaults();
    fault2.setExpectedFault(Fault.OVERBURDEN);

    when(pwsFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of(fault1, fault2));

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getExpectedFaults())
        .usingRecursiveComparison()
        .isEqualTo(Set.of(fault1.getExpectedFault(), fault2.getExpectedFault()));
  }

  @Test
  void submissionDetailToForm_noExisting_holeCompletionTypes() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));
    when(holeCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of());

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getSelectedHoleCompletionTypes())
        .isEmpty();
    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getNumberOfHoleCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes());
  }

  @Test
  void submissionDetailToForm_existing_holeCompletionTypes() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var holeCompletionTypes1 = new HoleCompletionTypes();
    holeCompletionTypes1.setHoleCompletionType(HoleCompletionType.CASED_HOLE);
    holeCompletionTypes1.setNumberOfWellsWithHoleCompletionType(BigDecimal.ONE);

    var holeCompletionTypes2 = new HoleCompletionTypes();
    holeCompletionTypes2.setHoleCompletionType(HoleCompletionType.OPENED_HOLE);
    holeCompletionTypes2.setNumberOfWellsWithHoleCompletionType(BigDecimal.valueOf(5));

    var holeCompletionTypeList = List.of(holeCompletionTypes1, holeCompletionTypes2);

    when(holeCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(holeCompletionTypeList);

    var expectedSelectedMap = Map.of(holeCompletionTypes1.getHoleCompletionType(), true,
        holeCompletionTypes2.getHoleCompletionType(), true);
    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getSelectedHoleCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes();
    expectedNumberMap.put(holeCompletionTypes1.getHoleCompletionType(),
        DecimalInput.createDecimalInput(holeCompletionTypes1.getNumberOfWellsWithHoleCompletionType()));
    expectedNumberMap.put(holeCompletionTypes2.getHoleCompletionType(),
        DecimalInput.createDecimalInput(holeCompletionTypes2.getNumberOfWellsWithHoleCompletionType()));

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getNumberOfHoleCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedNumberMap);
  }

  @Test
  void submissionDetailToForm_noExisting_plannedCompletionTypes() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));
    when(plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of());

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getSelectedHoleCompletionTypes())
        .isEmpty();
    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getNumberOfHoleCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes());
  }

  @Test
  void submissionDetailToForm_existing_plannedCompletionTypes() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var plannedCompletionTypes1 = new PlannedCompletionTypes();
    plannedCompletionTypes1.setPlannedCompletionType(PlannedCompletionType.CEMENTED_LINER);
    plannedCompletionTypes1.setNumberOfWellsWithPlannedCompletionType(BigDecimal.ONE);

    var plannedCompletionTypes2 = new PlannedCompletionTypes();
    plannedCompletionTypes2.setPlannedCompletionType(PlannedCompletionType.GRAVELPACK);
    plannedCompletionTypes2.setNumberOfWellsWithPlannedCompletionType(BigDecimal.valueOf(5));

    var plannedCompletionTypeList = List.of(plannedCompletionTypes1, plannedCompletionTypes2);

    when(plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(plannedCompletionTypeList);

    var expectedSelectedMap = Map.of(plannedCompletionTypes1.getPlannedCompletionType(), true,
        plannedCompletionTypes2.getPlannedCompletionType(), true);
    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getSelectedPlannedCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfPlannedCompletionTypes();
    expectedNumberMap.put(plannedCompletionTypes1.getPlannedCompletionType(),
        DecimalInput.createDecimalInput(plannedCompletionTypes1.getNumberOfWellsWithPlannedCompletionType()));
    expectedNumberMap.put(plannedCompletionTypes2.getPlannedCompletionType(),
        DecimalInput.createDecimalInput(plannedCompletionTypes2.getNumberOfWellsWithPlannedCompletionType()));

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getNumberOfPlannedCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedNumberMap);
  }

  @Test
  void submissionDetailToForm_existing_plannedCompletionTypes_selectedOther() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var plannedCompletionTypes1 = new PlannedCompletionTypes();
    plannedCompletionTypes1.setPlannedCompletionType(PlannedCompletionType.OTHER);
    plannedCompletionTypes1.setNumberOfWellsWithPlannedCompletionType(BigDecimal.ONE);
    plannedCompletionTypes1.setOtherPlannedCompletionType("My planned completion type");

    var plannedCompletionTypeList = List.of(plannedCompletionTypes1);

    when(plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(plannedCompletionTypeList);

    var plannedSanctionedWellsForm = plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail);

    // check selection
    var expectedSelectedMap = Map.of(plannedCompletionTypes1.getPlannedCompletionType(), true);
    assertThat(plannedSanctionedWellsForm.getSelectedPlannedCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    // check other text
    assertThat(plannedSanctionedWellsForm.getOtherPlannedCompletionType())
        .isEqualTo(plannedCompletionTypes1.getOtherPlannedCompletionType());

    // check number of wells
    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfPlannedCompletionTypes();
    expectedNumberMap.put(plannedCompletionTypes1.getPlannedCompletionType(),
        DecimalInput.createDecimalInput(plannedCompletionTypes1.getNumberOfWellsWithPlannedCompletionType()));

    assertThat(plannedSanctionedWellsForm.getNumberOfPlannedCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedNumberMap);
  }

  @Test
  void submissionDetailToForm_noExisting_artificialLiftMechanisms() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));
    when(artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of());

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getSelectedArtificialLiftMechanisms())
        .isEmpty();
    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getNumberOfArtificialLiftMechanisms())
        .usingRecursiveComparison()
        .isEqualTo(PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms());
  }

  @Test
  void submissionDetailToForm_existing_artificialLiftMechanisms() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var artificialLiftMechanisms1 = new ArtificialLiftMechanisms();
    artificialLiftMechanisms1.setArtificialLiftMechanism(ArtificialLiftMechanism.GAS_LIFT);
    artificialLiftMechanisms1.setNumberOfWellsWithArtificialLiftMechanism(BigDecimal.ONE);

    var artificialLiftMechanisms2 = new ArtificialLiftMechanisms();
    artificialLiftMechanisms2.setArtificialLiftMechanism(ArtificialLiftMechanism.ELECTRIC_SUBMERSIBLE_PUMP);
    artificialLiftMechanisms2.setNumberOfWellsWithArtificialLiftMechanism(BigDecimal.valueOf(5));

    var holeCompletionTypeList = List.of(artificialLiftMechanisms1, artificialLiftMechanisms2);

    when(artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(holeCompletionTypeList);

    var expectedSelectedMap = Map.of(artificialLiftMechanisms1.getArtificialLiftMechanism(), true,
        artificialLiftMechanisms2.getArtificialLiftMechanism(), true);
    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getSelectedArtificialLiftMechanisms())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms();
    expectedNumberMap.put(artificialLiftMechanisms1.getArtificialLiftMechanism(),
        DecimalInput.createDecimalInput(artificialLiftMechanisms1.getNumberOfWellsWithArtificialLiftMechanism()));
    expectedNumberMap.put(artificialLiftMechanisms2.getArtificialLiftMechanism(),
        DecimalInput.createDecimalInput(artificialLiftMechanisms2.getNumberOfWellsWithArtificialLiftMechanism()));

    assertThat(plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail).getNumberOfArtificialLiftMechanisms())
        .usingRecursiveComparison()
        .isEqualTo(expectedNumberMap);
  }

  @Test
  void submissionDetailToForm_existing_artificialLiftMechanisms_selectedOther() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));

    var artificialLiftMechanisms1 = new ArtificialLiftMechanisms();
    artificialLiftMechanisms1.setArtificialLiftMechanism(ArtificialLiftMechanism.OTHER);
    artificialLiftMechanisms1.setNumberOfWellsWithArtificialLiftMechanism(BigDecimal.ONE);
    artificialLiftMechanisms1.setOtherArtificialLiftMechanism("My artificial lift mechanism");

    var holeCompletionTypeList = List.of(artificialLiftMechanisms1);

    when(artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(holeCompletionTypeList);

    var plannedSanctionedWellsForm = plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail);

    // check selection
    var expectedSelectedMap = Map.of(artificialLiftMechanisms1.getArtificialLiftMechanism(), true);
    assertThat(plannedSanctionedWellsForm.getSelectedArtificialLiftMechanisms())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    // check other text
    assertThat(plannedSanctionedWellsForm.getOtherArtificialLiftMechanism())
        .isEqualTo(artificialLiftMechanisms1.getOtherArtificialLiftMechanism());

    // check number of wells
    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms();
    expectedNumberMap.put(artificialLiftMechanisms1.getArtificialLiftMechanism(),
        DecimalInput.createDecimalInput(artificialLiftMechanisms1.getNumberOfWellsWithArtificialLiftMechanism()));

    assertThat(plannedSanctionedWellsForm.getNumberOfArtificialLiftMechanisms())
        .usingRecursiveComparison()
        .isEqualTo(expectedNumberMap);
  }
}
