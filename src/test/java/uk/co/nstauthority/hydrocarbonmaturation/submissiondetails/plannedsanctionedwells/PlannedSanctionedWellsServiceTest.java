package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class PlannedSanctionedWellsServiceTest {

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
  private PlannedSanctionedWellsService plannedSanctionedWellsService;

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

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail))
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
    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail))
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

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail))
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
    when(pswFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of());

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getExpectedFaults())
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

    when(pswFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of(fault1, fault2));

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getExpectedFaults())
        .usingRecursiveComparison()
        .isEqualTo(Set.of(fault1.getExpectedFault(), fault2.getExpectedFault()));
  }

  @Test
  void submissionDetailToForm_noExisting_holeCompletionTypes() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));
    when(holeCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of());

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getSelectedHoleCompletionTypes())
        .isEmpty();
    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getNumberOfHoleCompletionTypes())
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
    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getSelectedHoleCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes();
    expectedNumberMap.put(holeCompletionTypes1.getHoleCompletionType(),
        DecimalInput.createDecimalInput(holeCompletionTypes1.getNumberOfWellsWithHoleCompletionType()));
    expectedNumberMap.put(holeCompletionTypes2.getHoleCompletionType(),
        DecimalInput.createDecimalInput(holeCompletionTypes2.getNumberOfWellsWithHoleCompletionType()));

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getNumberOfHoleCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedNumberMap);
  }

  @Test
  void submissionDetailToForm_noExisting_plannedCompletionTypes() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedSanctionedWells));
    when(plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(List.of());

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getSelectedHoleCompletionTypes())
        .isEmpty();
    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getNumberOfHoleCompletionTypes())
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
    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getSelectedPlannedCompletionTypes())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfPlannedCompletionTypes();
    expectedNumberMap.put(plannedCompletionTypes1.getPlannedCompletionType(),
        DecimalInput.createDecimalInput(plannedCompletionTypes1.getNumberOfWellsWithPlannedCompletionType()));
    expectedNumberMap.put(plannedCompletionTypes2.getPlannedCompletionType(),
        DecimalInput.createDecimalInput(plannedCompletionTypes2.getNumberOfWellsWithPlannedCompletionType()));

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getNumberOfPlannedCompletionTypes())
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

    var plannedSanctionedWellsForm = plannedSanctionedWellsService.submissionDetailToForm(submissionDetail);

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

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getSelectedArtificialLiftMechanisms())
        .isEmpty();
    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getNumberOfArtificialLiftMechanisms())
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
    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getSelectedArtificialLiftMechanisms())
        .usingRecursiveComparison()
        .isEqualTo(expectedSelectedMap);

    var expectedNumberMap = PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms();
    expectedNumberMap.put(artificialLiftMechanisms1.getArtificialLiftMechanism(),
        DecimalInput.createDecimalInput(artificialLiftMechanisms1.getNumberOfWellsWithArtificialLiftMechanism()));
    expectedNumberMap.put(artificialLiftMechanisms2.getArtificialLiftMechanism(),
        DecimalInput.createDecimalInput(artificialLiftMechanisms2.getNumberOfWellsWithArtificialLiftMechanism()));

    assertThat(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail).getNumberOfArtificialLiftMechanisms())
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

    var artificialLiftMechanismsList = List.of(artificialLiftMechanisms1);

    when(artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells))
        .thenReturn(artificialLiftMechanismsList);

    var plannedSanctionedWellsForm = plannedSanctionedWellsService.submissionDetailToForm(submissionDetail);

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

  @Test
  void savePlannedSanctionedWells_assertPlannedSanctionedWells() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var exampleForm = getExampleForm();
    var captor = ArgumentCaptor.forClass(PlannedSanctionedWells.class);
    plannedSanctionedWellsService.savePlannedSanctionedWells(exampleForm, submissionDetail);

    verify(plannedSanctionedWellsRepository, times(1)).save(captor.capture());
    verifyNoMoreInteractions(plannedSanctionedWellsRepository);

    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(UUID.class, SubmissionDetail.class)
        .isEqualTo(getExpectedPlannedPlannedSanctionedWellsForExampleForm());
  }

  @Test
  void savePlannedSanctionedWells_assertFaults() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    when(plannedSanctionedWellsRepository.save(any(PlannedSanctionedWells.class)))
        .thenReturn(plannedSanctionedWells);

    var exampleForm = getExampleForm();

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<PswFaults>> pswFaultsCaptor = ArgumentCaptor.forClass(List.class);

    plannedSanctionedWellsService.savePlannedSanctionedWells(exampleForm, submissionDetail);

    verify(plannedSanctionedWellsRepository, times(1)).save(any(PlannedSanctionedWells.class));
    verifyNoMoreInteractions(plannedSanctionedWellsRepository);

    verify(pswFaultRepository, times(1)).deleteAllByPlannedSanctionedWells(any(PlannedSanctionedWells.class));
    verify(pswFaultRepository, times(1)).saveAll(pswFaultsCaptor.capture());

    assertThat(pswFaultsCaptor.getValue())
        .extracting(
            PswFaults::getExpectedFault
        ).containsExactlyElementsOf(
            exampleForm.getExpectedFaults()
        );
  }

  @Test
  void savePlannedSanctionedWells_assertHoleCompletionTypes() {
    var exampleForm = getExampleForm();
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    when(plannedSanctionedWellsRepository.save(any(PlannedSanctionedWells.class)))
        .thenReturn(plannedSanctionedWells);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<HoleCompletionTypes>> holeCompletionTypeCaptor = ArgumentCaptor.forClass(List.class);

    plannedSanctionedWellsService.savePlannedSanctionedWells(exampleForm, submissionDetail);

    verify(plannedSanctionedWellsRepository, times(1)).save(any(PlannedSanctionedWells.class));
    verifyNoMoreInteractions(plannedSanctionedWellsRepository);

    verify(holeCompletionTypesRepository, times(1)).deleteAllByPlannedSanctionedWells(any(PlannedSanctionedWells.class));
    verify(holeCompletionTypesRepository, times(1)).saveAll(holeCompletionTypeCaptor.capture());

    assertThat(holeCompletionTypeCaptor.getValue())
        .extracting(
            HoleCompletionTypes::getHoleCompletionType,
            HoleCompletionTypes::getNumberOfWellsWithHoleCompletionType
        ).contains(
            Tuple.tuple(HoleCompletionType.OPENED_HOLE, BigDecimal.valueOf(4)),
            Tuple.tuple(HoleCompletionType.CASED_HOLE, BigDecimal.valueOf(6))
        );
  }

  @Test
  void savePlannedSanctionedWells_assertPlannedCompletionTypes() {
    var exampleForm = getExampleForm();
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    when(plannedSanctionedWellsRepository.save(any(PlannedSanctionedWells.class)))
        .thenReturn(plannedSanctionedWells);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<PlannedCompletionTypes>> plannedCompletionTypeCaptor = ArgumentCaptor.forClass(List.class);

    plannedSanctionedWellsService.savePlannedSanctionedWells(exampleForm, submissionDetail);

    verify(plannedSanctionedWellsRepository, times(1)).save(any(PlannedSanctionedWells.class));
    verifyNoMoreInteractions(plannedSanctionedWellsRepository);

    verify(plannedCompletionTypesRepository, times(1)).deleteAllByPlannedSanctionedWells(any(PlannedSanctionedWells.class));
    verify(plannedCompletionTypesRepository, times(1)).saveAll(plannedCompletionTypeCaptor.capture());

    assertThat(plannedCompletionTypeCaptor.getValue())
        .extracting(
            PlannedCompletionTypes::getPlannedCompletionType,
            PlannedCompletionTypes::getNumberOfWellsWithPlannedCompletionType,
            PlannedCompletionTypes::getOtherPlannedCompletionType
        ).contains(
            Tuple.tuple(PlannedCompletionType.GRAVELPACK, BigDecimal.valueOf(6), null),
            Tuple.tuple(PlannedCompletionType.OTHER, BigDecimal.valueOf(4), exampleForm.getOtherPlannedCompletionType())
        );
  }

  @Test
  void savePlannedSanctionedWells_assertArtificialLiftMechanisms() {
    var exampleForm = getExampleForm();
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    when(plannedSanctionedWellsRepository.save(any(PlannedSanctionedWells.class)))
        .thenReturn(plannedSanctionedWells);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<ArtificialLiftMechanisms>> artificialLiftMechanismsCaptor =
        ArgumentCaptor.forClass(List.class);

    plannedSanctionedWellsService.savePlannedSanctionedWells(exampleForm, submissionDetail);

    verify(plannedSanctionedWellsRepository, times(1)).save(any(PlannedSanctionedWells.class));
    verifyNoMoreInteractions(plannedSanctionedWellsRepository);

    verify(artificialLiftMechanismsRepository, times(1)).deleteAllByPlannedSanctionedWells(any(PlannedSanctionedWells.class));
    verify(artificialLiftMechanismsRepository, times(1)).saveAll(artificialLiftMechanismsCaptor.capture());

    assertThat(artificialLiftMechanismsCaptor.getValue())
        .extracting(
            ArtificialLiftMechanisms::getArtificialLiftMechanism,
            ArtificialLiftMechanisms::getNumberOfWellsWithArtificialLiftMechanism,
            ArtificialLiftMechanisms::getOtherArtificialLiftMechanism
        ).contains(
            Tuple.tuple(ArtificialLiftMechanism.ELECTRIC_SUBMERSIBLE_PUMP, BigDecimal.valueOf(6), null),
            Tuple.tuple(ArtificialLiftMechanism.OTHER, BigDecimal.valueOf(4), exampleForm.getOtherArtificialLiftMechanism())
        );
  }

  private PlannedSanctionedWellsForm getExampleForm() {
    var form = new PlannedSanctionedWellsForm();

    form.setTotalCapex(
        DecimalInput.createDecimalInput(10)
    );
    form.setTotalNumberOfPlannedSanctionedWells(
        DecimalInput.createDecimalInput(10)
    );
    form.setNumberOfInjectors(
        DecimalInput.createDecimalInput(5)
    );
    form.setNumberOfProducers(
        DecimalInput.createDecimalInput(3)
    );
    form.setNumberOfMonitors(
        DecimalInput.createDecimalInput(2)
    );
    form.setAnyHorizontalWells(true);
    form.setNumberOfHorizontalWells(
        DecimalInput.createDecimalInput(10)
    );
    form.setNumberOfHorizontalWellsWithErd(
        DecimalInput.createDecimalInput(5)
    );
    form.setAnyHighPressureTemperatureWells(true);
    form.setNumberOfHighPressureTemperatureWells(
        DecimalInput.createDecimalInput(10)
    );
    form.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    form.setNumberOfWellsWithinNarrowOrTightPpfgWindow(
        DecimalInput.createDecimalInput(10)
    );
    form.setIsManagedPressureDrillingUtilized(true);
    form.setAnyWellboreStabilityChallenges(true);
    form.setNumberOfWellboreStabilityChallenges(
        DecimalInput.createDecimalInput(10)
    );
    form.setStabilityChallengesInformation("challenges");
    form.setNumberOfWellsExpectedToCrossFaults(
        DecimalInput.createDecimalInput(10)
    );
    form.setAnyExpectedLossesFromDrilling(true);
    form.setWhyLossesExpected("reasons");
    form.setAnyExpectedDrillingChallenges(true);
    form.setDrillingChallengesMitigations("mitigations");
    form.setStimulationTypeRequired(StimulationType.HYDRAULIC_FRACTURING);
    form.getStimulationTypeDecimalInputHashMap()
        .put(StimulationType.HYDRAULIC_FRACTURING, DecimalInput.createDecimalInput(5));
    form.setIsExpectingFormationDamage(true);
    form.setExpectedFormationDamage("damage");
    form.setAnyHeavyOil(true);
    form.setAnySpecialisedDrillingEquipmentRequired(true);
    form.setSpecialisedDrillingEquipmentRequired("equipment");

    // set faults
    form.setAnyWellsExpectedToCrossFaults(true);
    form.setExpectedFaults(Set.of(Fault.RESERVOIR, Fault.OVERBURDEN));

    // set hole completion types
    form.setSelectedHoleCompletionTypes(Map.of(HoleCompletionType.OPENED_HOLE, true,
        HoleCompletionType.CASED_HOLE, true));
    form.setNumberOfHoleCompletionTypes(Map.of(HoleCompletionType.OPENED_HOLE, DecimalInput.createDecimalInput( 4) ,
        HoleCompletionType.CASED_HOLE, DecimalInput.createDecimalInput( 6)));

    // set planned completion types
    form.setSelectedPlannedCompletionTypes(Map.of(PlannedCompletionType.GRAVELPACK, true,
        PlannedCompletionType.OTHER, true));
    form.setNumberOfPlannedCompletionTypes(Map.of(PlannedCompletionType.GRAVELPACK, DecimalInput.createDecimalInput(6),
        PlannedCompletionType.OTHER, DecimalInput.createDecimalInput(4)));
    form.setOtherPlannedCompletionType("My completion type");

    // set artificial lift mechanisms
    form.setSelectedArtificialLiftMechanisms(Map.of(ArtificialLiftMechanism.ELECTRIC_SUBMERSIBLE_PUMP, true,
        ArtificialLiftMechanism.OTHER, true));
    form.setNumberOfArtificialLiftMechanisms(Map.of(ArtificialLiftMechanism.ELECTRIC_SUBMERSIBLE_PUMP, DecimalInput.createDecimalInput(6),
        ArtificialLiftMechanism.OTHER, DecimalInput.createDecimalInput(4)));
    form.setOtherArtificialLiftMechanism("My artificial lift mechanism");

    return form;
  }

  private PlannedSanctionedWells getExpectedPlannedPlannedSanctionedWellsForExampleForm() {
    var plannedSanctionedWells = new PlannedSanctionedWells();

    plannedSanctionedWells.setTotalCapex(
        BigDecimal.valueOf(10)
    );
    plannedSanctionedWells.setNumberOfPlannedSanctionedWells(
        BigDecimal.valueOf(10)
    );
    plannedSanctionedWells.setNumberOfInjectors(
        BigDecimal.valueOf(5)
    );
    plannedSanctionedWells.setNumberOfProducers(
        BigDecimal.valueOf(3)
    );
    plannedSanctionedWells.setNumberOfMonitors(
        BigDecimal.valueOf(2)
    );
    plannedSanctionedWells.setAnyHorizontalWells(true);
    plannedSanctionedWells.setNumberOfHorizontalWells(
        BigDecimal.valueOf(10)
    );
    plannedSanctionedWells.setNumberOfHorizontalWellsWithErd(
        BigDecimal.valueOf(5)
    );
    plannedSanctionedWells.setAnyHighPressureTemperatureWells(true);
    plannedSanctionedWells.setNumberOfHighPressureTemperatureWells(
        BigDecimal.valueOf(10)
    );
    plannedSanctionedWells.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    plannedSanctionedWells.setNumberOfWellsWithinNarrowOrTightPpfgWindow(
        BigDecimal.valueOf(10)
    );
    plannedSanctionedWells.setIsManagedPressureDrillingUtilized(true);
    plannedSanctionedWells.setAnyWellboreStabilityChallenges(true);
    plannedSanctionedWells.setNumberOfWellboreStabilityChallenges(
        BigDecimal.valueOf(10)
    );
    plannedSanctionedWells.setStabilityChallengesInformation("challenges");
    plannedSanctionedWells.setAnyWellsExpectedToCrossFaults(true);
    plannedSanctionedWells.setNumberOfWellsExpectedToCrossFaults(
        BigDecimal.valueOf(10)
    );
    plannedSanctionedWells.setAnyExpectedLossesFromDrilling(true);
    plannedSanctionedWells.setWhyLossesExpected("reasons");
    plannedSanctionedWells.setAnyExpectedDrillingChallenges(true);
    plannedSanctionedWells.setDrillingChallengesMitigations("mitigations");
    plannedSanctionedWells.setStimulationTypeRequired(StimulationType.HYDRAULIC_FRACTURING);
    plannedSanctionedWells.setNumberOfWellsRequiringStimulation(BigDecimal.valueOf(5));
    plannedSanctionedWells.setExpectingFormationDamage(true);
    plannedSanctionedWells.setExpectedFormationDamage("damage");
    plannedSanctionedWells.setAnyHeavyOil(true);
    plannedSanctionedWells.setAnySpecialisedDrillingEquipmentRequired(true);
    plannedSanctionedWells.setSpecialisedDrillingEquipmentRequired("equipment");

    return plannedSanctionedWells;
  }
}
