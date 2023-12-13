package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PlannedUnsanctionedWellsFormValidatorTest {
  private PlannedUnsanctionedWellsFormValidator plannedUnsanctionedWellsFormValidator;

  private PlannedUnsanctionedWellsForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    plannedUnsanctionedWellsFormValidator = new PlannedUnsanctionedWellsFormValidator();

    form = new PlannedUnsanctionedWellsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_validForm_thenNoErrors(ValidationType validationType) {
    var form = new PlannedUnsanctionedWellsForm();
    form.setTotalCapex(DecimalInput.createDecimalInput(10));
    form.setTotalNumberOfPlannedUnsanctionedWells(DecimalInput.createDecimalInput(5));

    assertTrue(plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertThat(bindingResult.getAllErrors()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_invalidForm_nonNumericValues_thenErrors(ValidationType validationType) {
    var form = new PlannedUnsanctionedWellsForm();
    form.setTotalNumberOfPlannedUnsanctionedWells(new DecimalInput("a"));
    form.setTotalCapex(new DecimalInput("b"));

    assertFalse(plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("totalNumberOfPlannedUnsanctionedWells.decimalInputValue",
                Set.of("totalNumberOfPlannedUnsanctionedWells.decimalInputValue.notNumeric")),
            entry("totalCapex.decimalInputValue",
                Set.of("totalCapex.decimalInputValue.notNumeric"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_invalidForm_invalidPrecision_thenErrors(ValidationType validationType) {
    var form = new PlannedUnsanctionedWellsForm();
    form.setTotalNumberOfPlannedUnsanctionedWells(new DecimalInput("5.8"));
    form.setTotalCapex(new DecimalInput("20.09324"));

    assertFalse(plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("totalNumberOfPlannedUnsanctionedWells.decimalInputValue",
                Set.of("totalNumberOfPlannedUnsanctionedWells.decimalInputValue.incorrectDecimalPlaces")),
            entry("totalCapex.decimalInputValue",
                Set.of("totalCapex.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_invalidForm_invalidNumeric_thenErrors(ValidationType validationType) {
    var form = new PlannedUnsanctionedWellsForm();
    form.setTotalNumberOfPlannedUnsanctionedWells(new DecimalInput("-58"));
    form.setTotalCapex(new DecimalInput("-10"));

    assertFalse(plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("totalNumberOfPlannedUnsanctionedWells.decimalInputValue",
                Set.of("totalNumberOfPlannedUnsanctionedWells.decimalInputValue.lessThanMinValue")),
            entry("totalCapex.decimalInputValue",
                Set.of("totalCapex.decimalInputValue.lessThanMinValue"))
        );
  }

  @Test
  void validateFull_emptyForm_thenErrors() {
    var form = new PlannedUnsanctionedWellsForm();

    assertFalse(plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("totalNumberOfPlannedUnsanctionedWells.decimalInputValue",
                Set.of("totalNumberOfPlannedUnsanctionedWells.decimalInputValue.required")),
            entry("totalCapex.decimalInputValue",
                Set.of("totalCapex.decimalInputValue.required"))
        );
  }

  @Test
  void validatePartial_emptyForm_thenNoErrors() {
    var form = new PlannedUnsanctionedWellsForm();

    assertTrue(plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(bindingResult.getAllErrors()).isEmpty();
  }
}