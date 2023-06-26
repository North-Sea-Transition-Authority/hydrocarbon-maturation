package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class GasFluidPropertyFormValidatorTest {

  private GasFluidPropertyFormValidator gasFluidPropertyFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
    gasFluidPropertyFormValidator = new GasFluidPropertyFormValidator();
  }

  @Test
  void supports_whenGasFluidPropertyFormClass_thenTrue(){
    var form = GasFluidPropertyForm.class;
    assertTrue(gasFluidPropertyFormValidator.supports(form));
  }

  @Test
  void supports_whenNotGasFluidPropertyFormClass_thenFalse(){
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(gasFluidPropertyFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    gasFluidPropertyFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_partialValidationInvalidForm_thenValidationErrors(ValidationType validationType) {
    var form = new GasFluidPropertyForm();
    form.setCondensatePresent(true);
    form.setGasCo2(new DecimalInput("200"));
    form.setGasN2(new DecimalInput("-10"));
    form.setGasSpecificGravity(new DecimalInput("something"));
    form.setGasH2s(new DecimalInput("something"));
    form.setCondensateGravity(new DecimalInput("-10"));
    form.setCondensateWax(new DecimalInput("-10"));
    form.setCondensateViscosity(new DecimalInput("-10"));
    form.setCondensateSulphur(new DecimalInput("-10"));
    form.setCondensateOtherInclWax(new DecimalInput("-10"));


    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasFluidPropertyFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasSpecificGravity.decimalInputValue", Set.of("gasSpecificGravity.decimalInputValue.notNumeric")),
            entry("gasN2.decimalInputValue", Set.of("gasN2.decimalInputValue.lessThanMinValue")),
            entry("gasCo2.decimalInputValue", Set.of("gasCo2.decimalInputValue.moreThanMaxValue")),
            entry("gasH2s.decimalInputValue", Set.of("gasH2s.decimalInputValue.notNumeric")),
            entry("condensateGravity.decimalInputValue", Set.of("condensateGravity.decimalInputValue.lessThanMinValue")),
            entry("condensateViscosity.decimalInputValue", Set.of("condensateViscosity.decimalInputValue.lessThanMinValue")),
            entry("condensateSulphur.decimalInputValue", Set.of("condensateSulphur.decimalInputValue.lessThanMinValue")),
            entry("condensateWax.decimalInputValue", Set.of("condensateWax.decimalInputValue.lessThanMinValue")),
            entry("condensateOtherInclWax.decimalInputValue", Set.of("condensateOtherInclWax.decimalInputValue.lessThanMinValue"))
        );
  }

  @Test
  void validate_fullValidationWhenEmptyForm_thenValidationErrors() {
    var form = new GasFluidPropertyForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasFluidPropertyFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasSpecificGravity.decimalInputValue", Set.of("gasSpecificGravity.decimalInputValue.required")),
            entry("gasN2.decimalInputValue", Set.of("gasN2.decimalInputValue.required")),
            entry("gasCo2.decimalInputValue", Set.of("gasCo2.decimalInputValue.required")),
            entry("gasH2s.decimalInputValue", Set.of("gasH2s.decimalInputValue.required")),
            entry("condensatePresent", Set.of("condensatePresent.required"))
        );
  }

  @Test
  void validate_fullValidationWhenEmptyFormWithCondensate_thenValidationErrors() {
    var form = new GasFluidPropertyForm();
    form.setCondensatePresent(true);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasFluidPropertyFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasSpecificGravity.decimalInputValue", Set.of("gasSpecificGravity.decimalInputValue.required")),
            entry("gasN2.decimalInputValue", Set.of("gasN2.decimalInputValue.required")),
            entry("gasCo2.decimalInputValue", Set.of("gasCo2.decimalInputValue.required")),
            entry("gasH2s.decimalInputValue", Set.of("gasH2s.decimalInputValue.required")),
            entry("condensateGravity.decimalInputValue", Set.of("condensateGravity.decimalInputValue.required")),
            entry("condensateViscosity.decimalInputValue", Set.of("condensateViscosity.decimalInputValue.required")),
            entry("condensateSulphur.decimalInputValue", Set.of("condensateSulphur.decimalInputValue.required")),
            entry("condensateWax.decimalInputValue", Set.of("condensateWax.decimalInputValue.required")),
            entry("condensateOtherInclWax.decimalInputValue", Set.of("condensateOtherInclWax.decimalInputValue.required"))
        );
  }

  private GasFluidPropertyForm getValidForm() {
    var form = new GasFluidPropertyForm();
    form.setGasN2(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setGasH2s(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setGasSpecificGravity(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setGasCo2(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setCondensatePresent(true);
    form.setCondensateSulphur(DecimalInput.createDecimalInput(BigDecimal.TEN));
    form.setCondensateViscosity(DecimalInput.createDecimalInput(BigDecimal.TEN));
    form.setCondensateOtherInclWax(DecimalInput.createDecimalInput(BigDecimal.TEN));
    form.setCondensateWax(DecimalInput.createDecimalInput(BigDecimal.TEN));
    form.setCondensateGravity(DecimalInput.createDecimalInput(BigDecimal.TEN));

    return form;
  }
}