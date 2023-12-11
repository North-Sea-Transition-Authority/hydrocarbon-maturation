package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PlannedSanctionedWellsFormValidatorTest {

  private PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator;

  private SubmissionDetail submissionDetail;
  private PlannedSanctionedWellsForm form;
  private BindingResult bindingResult;

  private static final String FIRST_HOLE_COMPLETION_TYPE_ID = "selectedHoleCompletionTypes[%s]"
      .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(HoleCompletionType.class));

  private static final String FIRST_PLANNED_COMPLETION_TYPE_ID = "selectedPlannedCompletionTypes[%s]"
      .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(PlannedCompletionType.class));

  private static final String FIRST_ARTIFICIAL_LIFT_MECHANISM_ID = "selectedArtificialLiftMechanisms[%s]"
      .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(ArtificialLiftMechanism.class));

  @BeforeEach
  void setUp() {
    plannedSanctionedWellsFormValidator = new PlannedSanctionedWellsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    form = validForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_validForm_thenNoErrors(ValidationType validationType) {
    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertThat(bindingResult.getAllErrors()).isEmpty();
  }

  @Test
  void validatePartial_emptyForm_thenNoErrors() {
    var emptyForm = new PlannedSanctionedWellsForm();
    var emptyBindingResult = new BeanPropertyBindingResult(emptyForm, "form");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(emptyForm, emptyBindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(bindingResult.getAllErrors()).isEmpty();
  }

  @Test
  void validateFull_emptyForm_thenErrors() {
    var emptyForm = new PlannedSanctionedWellsForm();
    var emptyBindingResult = new BeanPropertyBindingResult(emptyForm, "form");

    assertFalse(plannedSanctionedWellsFormValidator.isValid(emptyForm, emptyBindingResult, submissionDetail, ValidationType.FULL));

    var firstHoleCompletionTypeId =  "selectedHoleCompletionTypes[%s]"
        .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(HoleCompletionType.class));

    var firstPlannedCompletionTypeId =  "selectedPlannedCompletionTypes[%s]"
        .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(PlannedCompletionType.class));

    var firstArtificialLiftMechanismId =  "selectedArtificialLiftMechanisms[%s]"
        .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(ArtificialLiftMechanism.class));


    var extractedErrors = ValidatorTestingUtil.extractErrors(emptyBindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("totalCapex.decimalInputValue",
                Set.of("totalCapex.decimalInputValue.required")),
            entry("totalNumberOfPlannedSanctionedWells.decimalInputValue",
                Set.of("totalNumberOfPlannedSanctionedWells.decimalInputValue.required")),
            entry("numberOfInjectors.decimalInputValue",
                Set.of("numberOfInjectors.decimalInputValue.required")),
            entry("numberOfProducers.decimalInputValue",
                Set.of("numberOfProducers.decimalInputValue.required")),
            entry("numberOfMonitors.decimalInputValue",
                Set.of("numberOfMonitors.decimalInputValue.required")),
            entry("anyHorizontalWells",
                Set.of("anyHorizontalWells.required")),
            entry("anyHighPressureTemperatureWells",
                Set.of("anyHighPressureTemperatureWells.required")),
            entry("anyWellsWithinNarrowOrTightPpfgWindow",
                Set.of("anyWellsWithinNarrowOrTightPpfgWindow.required")),
            entry("anyWellboreStabilityChallenges",
                Set.of("anyWellboreStabilityChallenges.required")),
            entry("anyWellsExpectedToCrossFaults",
                Set.of("anyWellsExpectedToCrossFaults.required")),
            entry("anyExpectedLossesFromDrilling",
                Set.of("anyExpectedLossesFromDrilling.required")),
            entry("anyExpectedDrillingChallenges",
                Set.of("anyExpectedDrillingChallenges.required")),
            entry("stimulationTypeRequired",
                Set.of("stimulationTypeRequired.required")),
            entry("isExpectingFormationDamage",
                Set.of("isExpectingFormationDamage.required")),
            entry("anyHeavyOil",
                Set.of("anyHeavyOil.required")),
            entry("anySpecialisedDrillingEquipmentRequired",
                Set.of("anySpecialisedDrillingEquipmentRequired.required")),
            entry(firstHoleCompletionTypeId,
                Set.of("%s.required".formatted(firstHoleCompletionTypeId))),
            entry(firstPlannedCompletionTypeId,
                Set.of("%s.required".formatted(firstPlannedCompletionTypeId))),
            entry(firstArtificialLiftMechanismId,
                Set.of("%s.required".formatted(firstArtificialLiftMechanismId)))
        );
  }

  // total capex

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_totalCapex_valid_withPrecision(ValidationType validationType) {

    form.setTotalCapex(new DecimalInput("5.85"));
    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertThat(bindingResult.getAllErrors()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_totalCapex_invalid_wrongPrecision(ValidationType validationType) {

    form.setTotalCapex(new DecimalInput("5.805"));
    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("totalCapex.decimalInputValue",
                Set.of("totalCapex.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_totalCapex_invalid_negativeValue(ValidationType validationType) {

    form.setTotalCapex(DecimalInput.createDecimalInput(-5));
    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("totalCapex.decimalInputValue",
                Set.of("totalCapex.decimalInputValue.lessThanMinValue"))
        );
  }

  // well types and sum

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_wellTypesAndSum_valid(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));
    form.setNumberOfInjectors(DecimalInput.createDecimalInput(5));
    form.setNumberOfProducers(DecimalInput.createDecimalInput(3));
    form.setNumberOfMonitors(DecimalInput.createDecimalInput(2));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertThat(bindingResult.getAllErrors()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_wellTypesAndSum_invalid_incorrectSum(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(20));
    form.setNumberOfInjectors(DecimalInput.createDecimalInput(5));
    form.setNumberOfProducers(DecimalInput.createDecimalInput(3));
    form.setNumberOfMonitors(DecimalInput.createDecimalInput(2));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("totalNumberOfPlannedSanctionedWells.decimalInputValue",
                Set.of("totalNumberOfPlannedSanctionedWells.decimalInputValue.invalidTotals")),
            entry("numberOfInjectors.decimalInputValue",
                Set.of("numberOfInjectors.decimalInputValue.invalidTotals")),
            entry("numberOfProducers.decimalInputValue",
                Set.of("numberOfProducers.decimalInputValue.invalidTotals")),
            entry("numberOfMonitors.decimalInputValue",
                Set.of("numberOfMonitors.decimalInputValue.invalidTotals"))
        );
  }

  @Test
  void validatePartial_wellTypesAndSum_empty() {
    form.setTotalNumberOfPlannedSanctionedWells(new DecimalInput());
    form.setNumberOfInjectors(new DecimalInput());
    form.setNumberOfProducers(new DecimalInput());
    form.setNumberOfMonitors(new DecimalInput());

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(bindingResult.getAllErrors()).isEmpty();
  }

  @Test
  void validateFull_wellTypesAndSum_empty() {
    form.setTotalNumberOfPlannedSanctionedWells(new DecimalInput());
    form.setNumberOfInjectors(new DecimalInput());
    form.setNumberOfProducers(new DecimalInput());
    form.setNumberOfMonitors(new DecimalInput());

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("totalNumberOfPlannedSanctionedWells.decimalInputValue",
                Set.of("totalNumberOfPlannedSanctionedWells.decimalInputValue.required")),
            entry("numberOfInjectors.decimalInputValue",
                Set.of("numberOfInjectors.decimalInputValue.required")),
            entry("numberOfProducers.decimalInputValue",
                Set.of("numberOfProducers.decimalInputValue.required")),
            entry("numberOfMonitors.decimalInputValue",
                Set.of("numberOfMonitors.decimalInputValue.required"))
        );
  }

  // horizontal wells

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_horizontalWells_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyHorizontalWells(true);
    // should be less than or equal to total planned sanctioned wells
    form.setNumberOfHorizontalWells(DecimalInput.createDecimalInput(20));
    form.setNumberOfHorizontalWellsWithErd(DecimalInput.createDecimalInput(30));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("numberOfHorizontalWells.decimalInputValue",
                Set.of("numberOfHorizontalWells.decimalInputValue.moreThanMaxValue")),
            entry("numberOfHorizontalWellsWithErd.decimalInputValue",
                Set.of("numberOfHorizontalWellsWithErd.decimalInputValue.moreThanMaxValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_horizontalWells_invalidNumberOfHorizontalWellsWithErd(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyHorizontalWells(true);
    form.setNumberOfHorizontalWells(DecimalInput.createDecimalInput(10));
    form.setNumberOfHorizontalWellsWithErd(DecimalInput.createDecimalInput(30));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("numberOfHorizontalWellsWithErd.decimalInputValue",
                Set.of("numberOfHorizontalWellsWithErd.decimalInputValue.moreThanMaxValue"))
        ).doesNotContain(
            entry("numberOfHorizontalWells.decimalInputValue",
                Set.of("numberOfHorizontalWells.decimalInputValue.moreThanMaxValue"))
        );
  }

  // high pressure or temperature wells

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_highPressureOrTemperature_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyHighPressureTemperatureWells(true);
    form.setNumberOfHighPressureTemperatureWells(DecimalInput.createDecimalInput(10));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_highPressureOrTemperature_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyHighPressureTemperatureWells(true);
    form.setNumberOfHighPressureTemperatureWells(DecimalInput.createDecimalInput(20));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("numberOfHighPressureTemperatureWells.decimalInputValue",
                Set.of("numberOfHighPressureTemperatureWells.decimalInputValue.moreThanMaxValue"))
        );
  }

  // PPFG wells

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_PpfgWells_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    form.setNumberOfWellsWithinNarrowOrTightPpfgWindow(DecimalInput.createDecimalInput(10));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_PpfgWells_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    form.setNumberOfWellsWithinNarrowOrTightPpfgWindow(DecimalInput.createDecimalInput(20));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("numberOfWellsWithinNarrowOrTightPpfgWindow.decimalInputValue",
                Set.of("numberOfWellsWithinNarrowOrTightPpfgWindow.decimalInputValue.moreThanMaxValue"))
        );
  }

  @Test
  void validatePartial_PpfgWells_noMangedPressureDrillingUtilizedSelection() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    form.setNumberOfWellsWithinNarrowOrTightPpfgWindow(DecimalInput.createDecimalInput(10));
    form.setIsManagedPressureDrillingUtilized(null);

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_PpfgWells_noMangedPressureDrillingUtilizedSelection() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsWithinNarrowOrTightPpfgWindow(true);
    form.setNumberOfWellsWithinNarrowOrTightPpfgWindow(DecimalInput.createDecimalInput(10));
    form.setIsManagedPressureDrillingUtilized(null);

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("isManagedPressureDrillingUtilized",
                Set.of("isManagedPressureDrillingUtilized.required"))
        );
  }

  // wellbore stability

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_wellboreStability_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellboreStabilityChallenges(true);
    form.setNumberOfWellboreStabilityChallenges(DecimalInput.createDecimalInput(1));
    form.setStabilityChallengesInformation("information");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_wellboreStability_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellboreStabilityChallenges(true);
    form.setNumberOfWellboreStabilityChallenges(DecimalInput.createDecimalInput(0));
    form.setStabilityChallengesInformation("information");

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("numberOfWellboreStabilityChallenges.decimalInputValue",
                Set.of("numberOfWellboreStabilityChallenges.decimalInputValue.lessThanMinValue"))
        );
  }

  @Test
  void validatePartial_wellboreStability_invalidStabilityChallengesInformation() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellboreStabilityChallenges(true);
    form.setNumberOfWellboreStabilityChallenges(DecimalInput.createDecimalInput(10));
    form.setStabilityChallengesInformation("");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_wellboreStability_invalidStabilityChallengesInformation() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellboreStabilityChallenges(true);
    form.setNumberOfWellboreStabilityChallenges(DecimalInput.createDecimalInput(10));
    form.setStabilityChallengesInformation("");

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("stabilityChallengesInformation",
                Set.of("stabilityChallengesInformation.required"))
        );
  }

  // fault crossings

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_faultCrossings_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsExpectedToCrossFaults(true);
    form.setNumberOfWellsExpectedToCrossFaults(DecimalInput.createDecimalInput(1));
    form.setExpectedFaults(Set.of(Fault.RESERVOIR, Fault.OVERBURDEN));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_faultCrossings_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsExpectedToCrossFaults(true);
    form.setNumberOfWellsExpectedToCrossFaults(DecimalInput.createDecimalInput(-5));
    form.setExpectedFaults(Set.of(Fault.RESERVOIR, Fault.OVERBURDEN));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("numberOfWellsExpectedToCrossFaults.decimalInputValue",
                Set.of("numberOfWellsExpectedToCrossFaults.decimalInputValue.lessThanMinValue"))
        );
  }

  @Test
  void validatePartial_faultCrossings_noWhereFaultsExpectedSelected() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsExpectedToCrossFaults(true);
    form.setNumberOfWellsExpectedToCrossFaults(DecimalInput.createDecimalInput(10));
    form.setExpectedFaults(Set.of());

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_faultCrossings_noWhereFaultsExpectedSelected() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setAnyWellsExpectedToCrossFaults(true);
    form.setNumberOfWellsExpectedToCrossFaults(DecimalInput.createDecimalInput(10));
    form.setExpectedFaults(Set.of());

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("expectedFaults",
                Set.of("expectedFaults.required"))
        );
  }

  // expected losses

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_expectedLosses_validWhyLossesExpected(ValidationType validationType) {
    form.setAnyExpectedLossesFromDrilling(true);
    form.setWhyLossesExpected("They just are");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validatePartial_expectedLosses_invalidWhyLossesExpected() {
    form.setAnyExpectedLossesFromDrilling(true);
    form.setWhyLossesExpected("");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_expectedLosses_invalidWhyLossesExpected() {
    form.setAnyExpectedLossesFromDrilling(true);
    form.setWhyLossesExpected("");

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("whyLossesExpected",
                Set.of("whyLossesExpected.required"))
        );
  }

  // expected drilling challenges

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_expectedDrillingChallenges_validMitigations(ValidationType validationType) {
    form.setAnyExpectedDrillingChallenges(true);
    form.setDrillingChallengesMitigations("all the mitigations needed");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validatePartial_expectedDrillingChallenges_invalidMitigations() {
    form.setAnyExpectedDrillingChallenges(true);
    form.setDrillingChallengesMitigations("");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_expectedDrillingChallenges_invalidMitigations() {
    form.setAnyExpectedDrillingChallenges(true);
    form.setDrillingChallengesMitigations("");

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("drillingChallengesMitigations",
                Set.of("drillingChallengesMitigations.required"))
        );
  }

  // stimulation types

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_stimulationTypes_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setStimulationTypeRequired(StimulationType.HYDRAULIC_FRACTURING);
    form.setStimulationTypeDecimalInputHashMap(
        Map.of(StimulationType.HYDRAULIC_FRACTURING, DecimalInput.createDecimalInput(5)));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_stimulationTypes_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setStimulationTypeRequired(StimulationType.HYDRAULIC_FRACTURING);
    form.setStimulationTypeDecimalInputHashMap(
        Map.of(StimulationType.HYDRAULIC_FRACTURING, DecimalInput.createDecimalInput(100)));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("stimulationTypeDecimalInputHashMap[HYDRAULIC_FRACTURING].decimalInputValue",
                Set.of("stimulationTypeDecimalInputHashMap[HYDRAULIC_FRACTURING].decimalInputValue.moreThanMaxValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_stimulationTypes_noStimulationTypeRequired(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setStimulationTypeRequired(StimulationType.NO_WELL_STIMULATION);
    form.setStimulationTypeDecimalInputHashMap(PlannedSanctionedWellsForm.emptyNumberOfStimulationTypes());

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  // formation damage

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_expectedFormationDamage_validExpectedFormationDamage(ValidationType validationType) {
    form.setIsExpectingFormationDamage(true);
    form.setExpectedFormationDamage("Horrendouss damage");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validatePartial_expectedFormationDamage_invalidExpectedFormationDamage() {
    form.setIsExpectingFormationDamage(true);
    form.setExpectedFormationDamage("");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_expectedFormationDamage_invalidExpectedFormationDamage() {
    form.setIsExpectingFormationDamage(true);
    form.setExpectedFormationDamage("");

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("expectedFormationDamage",
                Set.of("expectedFormationDamage.required"))
        );
  }

  // specialised drilling equipment

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_specialisedDrillingEquipment_validSpecialisedDrillingEquipment(ValidationType validationType) {
    form.setAnySpecialisedDrillingEquipmentRequired(true);
    form.setSpecialisedDrillingEquipmentRequired("Horrendouss damage");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validatePartial_specialisedDrillingEquipment_invalidSpecialisedDrillingEquipment() {
    form.setAnySpecialisedDrillingEquipmentRequired(true);
    form.setSpecialisedDrillingEquipmentRequired("");

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_specialisedDrillingEquipment_invalidSpecialisedDrillingEquipment() {
    form.setAnySpecialisedDrillingEquipmentRequired(true);
    form.setSpecialisedDrillingEquipmentRequired("");

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("specialisedDrillingEquipmentRequired",
                Set.of("specialisedDrillingEquipmentRequired.required"))
        );
  }

  // hole completion types

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_holeCompletionType_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, true,
            HoleCompletionType.CASED_HOLE, true
        ));
    form.setNumberOfHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, DecimalInput.createDecimalInput(5),
            HoleCompletionType.CASED_HOLE, DecimalInput.createDecimalInput(5)
        ));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_holeCompletionType_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, true,
            HoleCompletionType.CASED_HOLE, true
        ));
    form.setNumberOfHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, DecimalInput.createDecimalInput(3),
            HoleCompletionType.CASED_HOLE, DecimalInput.createDecimalInput(5)
        ));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry(FIRST_HOLE_COMPLETION_TYPE_ID,
                Set.of("%s.invalidTotals".formatted(FIRST_HOLE_COMPLETION_TYPE_ID)))
        );
  }

  @Test
  void validatePartial_holeCompletionType_noSelection() {
    form.setSelectedHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, false,
            HoleCompletionType.CASED_HOLE, false
        ));
    form.setNumberOfHoleCompletionTypes(PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes());

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_holeCompletionType_noSelection() {
    form.setSelectedHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, false,
            HoleCompletionType.CASED_HOLE, false
        ));
    form.setNumberOfHoleCompletionTypes(PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes());

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry(FIRST_HOLE_COMPLETION_TYPE_ID,
                Set.of("%s.required".formatted(FIRST_HOLE_COMPLETION_TYPE_ID)))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_holeCompletionType_notNumericInputs(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, true,
            HoleCompletionType.CASED_HOLE, true
        ));
    form.setNumberOfHoleCompletionTypes(
        Map.of(
            HoleCompletionType.OPENED_HOLE, new DecimalInput("a"),
            HoleCompletionType.CASED_HOLE, new DecimalInput("b")
        ));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("numberOfHoleCompletionTypes[OPENED_HOLE].decimalInputValue",
                Set.of("numberOfHoleCompletionTypes[OPENED_HOLE].decimalInputValue.notNumeric")),
            entry("numberOfHoleCompletionTypes[CASED_HOLE].decimalInputValue",
                Set.of("numberOfHoleCompletionTypes[CASED_HOLE].decimalInputValue.notNumeric"))
        );
  }

  // planned completion types

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_plannedCompletionType_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, true,
            PlannedCompletionType.CEMENTED_LINER, true
        ));
    form.setNumberOfPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, DecimalInput.createDecimalInput(5),
            PlannedCompletionType.CEMENTED_LINER, DecimalInput.createDecimalInput(5)
        ));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_plannedCompletionType_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, true,
            PlannedCompletionType.CEMENTED_LINER, true
        ));
    form.setNumberOfPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, DecimalInput.createDecimalInput(3),
            PlannedCompletionType.CEMENTED_LINER, DecimalInput.createDecimalInput(5)
        ));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry(FIRST_PLANNED_COMPLETION_TYPE_ID,
                Set.of("%s.invalidTotals".formatted(FIRST_PLANNED_COMPLETION_TYPE_ID)))
        );
  }

  @Test
  void validatePartial_plannedCompletionType_noSelection() {
    form.setSelectedPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, false,
            PlannedCompletionType.CEMENTED_LINER, false
        ));
    form.setNumberOfPlannedCompletionTypes(PlannedSanctionedWellsForm.emptyNumberOfPlannedCompletionTypes());

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_plannedCompletionType_noSelection() {
    form.setSelectedPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, false,
            PlannedCompletionType.CEMENTED_LINER, false
        ));
    form.setNumberOfPlannedCompletionTypes(PlannedSanctionedWellsForm.emptyNumberOfPlannedCompletionTypes());

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry(FIRST_PLANNED_COMPLETION_TYPE_ID,
                Set.of("%s.required".formatted(FIRST_PLANNED_COMPLETION_TYPE_ID)))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_plannedCompletionType_notNumericInputs(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, true,
            PlannedCompletionType.CEMENTED_LINER, true
        ));
    form.setNumberOfPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.GRAVELPACK, new DecimalInput("a"),
            PlannedCompletionType.CEMENTED_LINER, new DecimalInput("b")
        ));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("numberOfPlannedCompletionTypes[CEMENTED_LINER].decimalInputValue",
                Set.of("numberOfPlannedCompletionTypes[CEMENTED_LINER].decimalInputValue.notNumeric")),
            entry("numberOfPlannedCompletionTypes[GRAVELPACK].decimalInputValue",
                Set.of("numberOfPlannedCompletionTypes[GRAVELPACK].decimalInputValue.notNumeric"))
        );
  }

  @Test
  void validatePartial_plannedCompletionType_otherTypeEmptyName() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));
    form.setOtherPlannedCompletionType("");
    form.setSelectedPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.OTHER, true
        ));
    form.setNumberOfPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.OTHER, DecimalInput.createDecimalInput(10)
        ));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_plannedCompletionType_otherTypeEmptyName() {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));
    form.setOtherPlannedCompletionType("");
    form.setSelectedPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.OTHER, true
        ));
    form.setNumberOfPlannedCompletionTypes(
        Map.of(
            PlannedCompletionType.OTHER, DecimalInput.createDecimalInput(10)
        ));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("otherPlannedCompletionType",
                Set.of("otherPlannedCompletionType.required"))
        );
  }

  // artificial lift mechanisms

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_artificialLiftMechanisms_validNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, true,
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, true
        ));
    form.setNumberOfArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, DecimalInput.createDecimalInput(5),
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, DecimalInput.createDecimalInput(5)
        ));

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_artificialLiftMechanisms_invalidNumberOfWells(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, true,
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, true
        ));
    form.setNumberOfArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, DecimalInput.createDecimalInput(3),
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, DecimalInput.createDecimalInput(5)
        ));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry(FIRST_ARTIFICIAL_LIFT_MECHANISM_ID,
                Set.of("%s.invalidTotals".formatted(FIRST_ARTIFICIAL_LIFT_MECHANISM_ID)))
        );
  }

  @Test
  void validatePartial_artificialLiftMechanisms_noSelection() {
    form.setSelectedArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, false,
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, false
        ));
    form.setNumberOfArtificialLiftMechanisms(PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms());

    assertTrue(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_artificialLiftMechanisms_noSelection() {
    form.setSelectedArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, false,
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, false
        ));
    form.setNumberOfArtificialLiftMechanisms(PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms());

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry(FIRST_ARTIFICIAL_LIFT_MECHANISM_ID,
                Set.of("%s.required".formatted(FIRST_ARTIFICIAL_LIFT_MECHANISM_ID)))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_artificialLiftMechanisms_notNumericInputs(ValidationType validationType) {
    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));

    form.setSelectedArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, true,
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, true
        ));
    form.setNumberOfArtificialLiftMechanisms(
        Map.of(
            ArtificialLiftMechanism.GAS_LIFT, new DecimalInput("a"),
            ArtificialLiftMechanism.HYDRAULIC_PUMPS, new DecimalInput("b")
        ));

    assertFalse(plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("numberOfArtificialLiftMechanisms[HYDRAULIC_PUMPS].decimalInputValue",
                Set.of("numberOfArtificialLiftMechanisms[HYDRAULIC_PUMPS].decimalInputValue.notNumeric")),
            entry("numberOfArtificialLiftMechanisms[GAS_LIFT].decimalInputValue",
                Set.of("numberOfArtificialLiftMechanisms[GAS_LIFT].decimalInputValue.notNumeric"))
        );
  }


  private PlannedSanctionedWellsForm validForm() {
    var form = new PlannedSanctionedWellsForm();

    form.setTotalNumberOfPlannedSanctionedWells(DecimalInput.createDecimalInput(10));
    form.setTotalCapex(DecimalInput.createDecimalInput(100));
    form.setNumberOfInjectors(DecimalInput.createDecimalInput(5));
    form.setNumberOfProducers(DecimalInput.createDecimalInput(3));
    form.setNumberOfMonitors(DecimalInput.createDecimalInput(2));
    form.setAnyHorizontalWells(false);
    form.setNumberOfHorizontalWells(new DecimalInput());
    form.setNumberOfHorizontalWellsWithErd(new DecimalInput());
    form.setAnyHighPressureTemperatureWells(false);
    form.setNumberOfHighPressureTemperatureWells(new DecimalInput());
    form.setAnyWellsWithinNarrowOrTightPpfgWindow(false);
    form.setNumberOfWellsWithinNarrowOrTightPpfgWindow(new DecimalInput());
    form.setIsManagedPressureDrillingUtilized(true);
    form.setAnyWellboreStabilityChallenges(false);
    form.setNumberOfWellboreStabilityChallenges(new DecimalInput());
    form.setStabilityChallengesInformation(null);
    form.setAnyWellsExpectedToCrossFaults(false);
    form.setNumberOfWellsExpectedToCrossFaults(new DecimalInput());
    form.setExpectedFaults(null);
    form.setAnyExpectedLossesFromDrilling(false);
    form.setWhyLossesExpected(null);
    form.setAnyExpectedDrillingChallenges(false);
    form.setDrillingChallengesMitigations(null);

    var validStimulationTypes = PlannedSanctionedWellsForm.emptyNumberOfStimulationTypes();
    validStimulationTypes.put(StimulationType.NO_WELL_STIMULATION, new DecimalInput());
    form.setStimulationTypeDecimalInputHashMap(validStimulationTypes);
    form.setStimulationTypeRequired(StimulationType.NO_WELL_STIMULATION);
    
    form.setIsExpectingFormationDamage(false);
    form.setExpectedFormationDamage(null);
    form.setAnyHeavyOil(false);
    form.setAnySpecialisedDrillingEquipmentRequired(false);
    form.setSpecialisedDrillingEquipmentRequired(null);

    form.setSelectedHoleCompletionTypes(Map.of(HoleCompletionType.OPENED_HOLE, true));
    var validHoleCompletionType = PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes();
    validHoleCompletionType.put(HoleCompletionType.OPENED_HOLE, DecimalInput.createDecimalInput(10));
    form.setNumberOfHoleCompletionTypes(validHoleCompletionType);

    form.setSelectedPlannedCompletionTypes(Map.of(PlannedCompletionType.GRAVELPACK, true));
    var validPlannedCompletionType = PlannedSanctionedWellsForm.emptyNumberOfPlannedCompletionTypes();
    validPlannedCompletionType.put(PlannedCompletionType.GRAVELPACK, DecimalInput.createDecimalInput(10));
    form.setNumberOfPlannedCompletionTypes(validPlannedCompletionType);
    form.setOtherPlannedCompletionType(null);

    form.setSelectedArtificialLiftMechanisms(Map.of(ArtificialLiftMechanism.GAS_LIFT, true));
    var validArtificialLiftMechanisms = PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms();
    validArtificialLiftMechanisms.put(ArtificialLiftMechanism.GAS_LIFT, DecimalInput.createDecimalInput(10));
    form.setNumberOfArtificialLiftMechanisms(validArtificialLiftMechanisms);
    form.setOtherArtificialLiftMechanism(null);

    return form;
  }
}