package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCaseFormValidatorTest {

  private HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    hydrocarbonCaseFormValidator = new HydrocarbonCaseFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenGeneralDetailsFormClass_thenTrue() {
    var supportedClass = HydrocarbonCaseForm.class;
    assertTrue(hydrocarbonCaseFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotGeneralDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(hydrocarbonCaseFormValidator.supports(nonSupportedClass));
  }


  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();
    var bindingResult = validateHydrocarbonCaseForm_fullValidation(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenCaseTypesAllNull_thenValidationErrors() {
    var form = new HydrocarbonCaseForm();
    var bindingResult = validateHydrocarbonCaseForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("oilOnlyCaseType", Set.of("oilOnlyCaseType.required")),
            entry("gasOnlyCaseType", Set.of("gasOnlyCaseType.required")),
            entry("oilAndGasCaseType", Set.of("oilAndGasCaseType.required"))
        );
  }

  @Test
  void validate_whenProbabilityIsNullAndFullValidation_thenValidationErrors() {
    var form = new HydrocarbonCaseForm();
    form.setOilAndGasCaseType(true);
    form.setOilAndGasProbability(new DecimalInput());
    form.setGasOnlyCaseType(true);
    form.setGasOnlyProbability(new DecimalInput());
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput());

    var bindingResult = validateHydrocarbonCaseForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("oilOnlyCaseType", Set.of("oilOnlyCaseType.decimalInputError")),
            entry("oilOnlyProbability.decimalInputValue", Set.of("oilOnlyProbability.decimalInputValue.required")),
            entry("gasOnlyProbability.decimalInputValue", Set.of("gasOnlyProbability.decimalInputValue.required")),
            entry("oilAndGasProbability.decimalInputValue", Set.of("oilAndGasProbability.decimalInputValue.required"))
        );
  }

  @Test
  void validate_whenAddsUpToLessThan100FullValidation_thenErrors() {
    var form = new HydrocarbonCaseForm();
    form.setOilAndGasCaseType(true);
    form.setOilAndGasProbability(new DecimalInput("5"));
    form.setGasOnlyCaseType(true);
    form.setGasOnlyProbability(new DecimalInput("25"));
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput("50"));

    var bindingResult = validateHydrocarbonCaseForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("oilOnlyCaseType", Set.of("oilOnlyCaseType.decimalInputError"))
        );
  }

  @Test
  void validate_whenProbabilityIsNullAndPartialValidation_thenNoValidationErrors() {
    var form = new HydrocarbonCaseForm();
    form.setOilAndGasCaseType(true);
    form.setOilAndGasProbability(new DecimalInput());
    form.setGasOnlyCaseType(true);
    form.setGasOnlyProbability(new DecimalInput());
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput());

    var bindingResult = validateHydrocarbonCaseForm_partialValidation(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_partialValidationInvalidForm_thenValidationErrors() {
    var form = new HydrocarbonCaseForm();
    form.setOilAndGasCaseType(true);
    form.setOilAndGasProbability(new DecimalInput("200"));
    form.setGasOnlyCaseType(true);
    form.setGasOnlyProbability(new DecimalInput("-10"));
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput("aflhlkasjf"));

    var bindingResult = validateHydrocarbonCaseForm_partialValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("oilOnlyProbability.decimalInputValue", Set.of("oilOnlyProbability.decimalInputValue.notNumeric")),
            entry("gasOnlyProbability.decimalInputValue", Set.of("gasOnlyProbability.decimalInputValue.lessThanMinValue")),
            entry("oilAndGasProbability.decimalInputValue", Set.of("oilAndGasProbability.decimalInputValue.moreThanMaxValue"))
        );
  }

  private BindingResult validateHydrocarbonCaseForm_fullValidation(HydrocarbonCaseForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    hydrocarbonCaseFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));
    return bindingResult;
  }

  private BindingResult validateHydrocarbonCaseForm_partialValidation(HydrocarbonCaseForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    hydrocarbonCaseFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail));
    return bindingResult;
  }

    private HydrocarbonCaseForm getValidForm() {
    var form = new HydrocarbonCaseForm();
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput("10"));
    form.setGasOnlyCaseType(true);
    form.setGasOnlyProbability(new DecimalInput("50"));
    form.setOilAndGasCaseType(true);
    form.setOilAndGasProbability(new DecimalInput("40"));
    return form;
  }
}