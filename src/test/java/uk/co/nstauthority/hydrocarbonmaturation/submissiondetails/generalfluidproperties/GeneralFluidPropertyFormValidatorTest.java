package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class GeneralFluidPropertyFormValidatorTest {

  private GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    generalFluidPropertyFormValidator = new GeneralFluidPropertyFormValidator();
  }

  @Test
  void supports_whenGeneralFluidPropertyFormClass_thenTrue(){
    var form = GeneralFluidPropertyForm.class;
    assertTrue(generalFluidPropertyFormValidator.supports(form));
  }

  @Test
  void supports_whenNotGeneralFluidPropertyFormClass_thenFalse(){
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(generalFluidPropertyFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalFluidPropertyFormValidator.validate(form, bindingResult, validationType);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_partialValidationInvalidForm_thenValidationErrors(ValidationType validationType) {
    var form = new GeneralFluidPropertyForm();
    form.setWaterSalinity(new DecimalInput("-200"));
    form.setDatumDepth(new DecimalInput("-10"));
    form.setTemperatureAtDatumDepth(new DecimalInput("something"));
    form.setPressureAtDatumDepth(new DecimalInput("something"));
    form.setSaturationPressureAtDatumDepth(new DecimalInput("something"));
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    generalFluidPropertyFormValidator.validate(form, bindingResult, validationType);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("datumDepth.decimalInputValue", Set.of("datumDepth.decimalInputValue.lessThanMinValue")),
            entry("temperatureAtDatumDepth.decimalInputValue", Set.of("temperatureAtDatumDepth.decimalInputValue.notNumeric")),
            entry("pressureAtDatumDepth.decimalInputValue", Set.of("pressureAtDatumDepth.decimalInputValue.notNumeric")),
            entry("saturationPressureAtDatumDepth.decimalInputValue", Set.of("saturationPressureAtDatumDepth.decimalInputValue.notNumeric")),
            entry("waterSalinity.decimalInputValue", Set.of("waterSalinity.decimalInputValue.lessThanMinValue"))
        );
  }

  @Test
  void validate_fullValidationWhenEmptyForm_thenValidationErrors() {
    var form = new GeneralFluidPropertyForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    generalFluidPropertyFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("datumDepth.decimalInputValue", Set.of("datumDepth.decimalInputValue.required")),
            entry("temperatureAtDatumDepth.decimalInputValue", Set.of("temperatureAtDatumDepth.decimalInputValue.required")),
            entry("pressureAtDatumDepth.decimalInputValue", Set.of("pressureAtDatumDepth.decimalInputValue.required")),
            entry("saturationPressureAtDatumDepth.decimalInputValue", Set.of("saturationPressureAtDatumDepth.decimalInputValue.required")),
            entry("waterSalinity.decimalInputValue", Set.of("waterSalinity.decimalInputValue.required"))
        );
  }

  private GeneralFluidPropertyForm getValidForm() {
    var form = new GeneralFluidPropertyForm();
    form.setWaterSalinity(new DecimalInput("1"));
    form.setDatumDepth(new DecimalInput("2"));
    form.setTemperatureAtDatumDepth(new DecimalInput("3"));
    form.setPressureAtDatumDepth(new DecimalInput("4"));
    form.setSaturationPressureAtDatumDepth(new DecimalInput("5"));
    return form;
  }
}