package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class OilFluidPropertyFormValidatorTest {

  private OilFluidPropertyFormValidator oilFluidPropertyFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    oilFluidPropertyFormValidator = new OilFluidPropertyFormValidator();
  }

  @Test
  void supports_whenOilFluidPropertyFormClass_thenTrue(){
    var form = OilFluidPropertyForm.class;
    assertTrue(oilFluidPropertyFormValidator.supports(form));
  }

  @Test
  void supports_whenNotOilFluidPropertyFormClass_thenFalse(){
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(oilFluidPropertyFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    oilFluidPropertyFormValidator.validate(form, bindingResult, validationType);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_partialValidationInvalidForm_thenValidationErrors(ValidationType validationType) {
    var form = new OilFluidPropertyForm();
    form.setOilWax(new DecimalInput("200.555"));
    form.setOilGravity(new DecimalInput("d"));
    form.setOilSulphur(new DecimalInput("-10"));
    form.setOilViscosity(new DecimalInput("something"));
    form.setOilOtherWaxAppearanceTemperature(new DecimalInput("something"));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilFluidPropertyFormValidator.validate(form, bindingResult, validationType);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilGravity.decimalInputValue", Set.of("oilGravity.decimalInputValue.notNumeric")),
            entry("oilSulphur.decimalInputValue", Set.of("oilSulphur.decimalInputValue.lessThanMinValue")),
            entry("oilWax.decimalInputValue", Set.of("oilWax.decimalInputValue.moreThanMaxValue")),
            entry("oilViscosity.decimalInputValue", Set.of("oilViscosity.decimalInputValue.notNumeric")),
            entry("oilOtherWaxAppearanceTemperature.decimalInputValue", Set.of("oilOtherWaxAppearanceTemperature.decimalInputValue.notNumeric"))
        );
  }

  @Test
  void validate_fullValidationWhenEmptyForm_thenValidationErrors() {
    var form = new OilFluidPropertyForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilFluidPropertyFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilGravity.decimalInputValue", Set.of("oilGravity.decimalInputValue.required")),
            entry("oilSulphur.decimalInputValue", Set.of("oilSulphur.decimalInputValue.required")),
            entry("oilWax.decimalInputValue", Set.of("oilWax.decimalInputValue.required")),
            entry("oilViscosity.decimalInputValue", Set.of("oilViscosity.decimalInputValue.required"))
        );
  }

  private OilFluidPropertyForm getValidForm() {
    var form = new OilFluidPropertyForm();
    form.setOilWax(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setOilViscosity(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setOilSulphur(DecimalInput.createDecimalInput(new BigDecimal("10")));
    form.setOilGravity(DecimalInput.createDecimalInput(new BigDecimal("25")));
    form.setOilOtherWaxAppearanceTemperature(DecimalInput.createDecimalInput(new BigDecimal("33")));
    return form;
  }
}