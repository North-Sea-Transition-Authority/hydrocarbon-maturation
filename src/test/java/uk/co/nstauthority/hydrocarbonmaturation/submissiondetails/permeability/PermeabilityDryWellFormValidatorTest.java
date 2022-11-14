package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PermeabilityDryWellFormValidatorTest {

  @InjectMocks
  private PermeabilityDryWellFormValidator permeabilityDryWellFormValidator;

  @Test
  void supports_whenPermeabilityDryWellFormClass_thenTrue() {
    var supportedClass = PermeabilityDryWellForm.class;
    assertTrue(permeabilityDryWellFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenPermeabilityDryWellFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(permeabilityDryWellFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withPermeability_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithPermeability();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityDryWellFormValidator.validate(form, bindingResult, validationType);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withoutPermeability_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithoutPermeability();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityDryWellFormValidator.validate(form, bindingResult, validationType);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenGivenForm_withIncorrectDecimalPlaces_thenValidationErrors(ValidationType validationType) {
    var form = getValidFormWithPermeability();
    form.getPermeabilityForm().setPermeabilityMax(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityForm().setPermeabilityMin(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityForm().setPermeabilityAverage(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityForm().setKvKhAverage(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityForm().setKvKhMax(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityForm().setKvKhMin(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityDryWellFormValidator.validate(form, bindingResult, validationType);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityForm.permeabilityMin.decimalInputValue", Set.of("permeabilityForm.permeabilityMin.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityForm.permeabilityMax.decimalInputValue", Set.of("permeabilityForm.permeabilityMax.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityForm.permeabilityAverage.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityForm.kvKhMin.decimalInputValue", Set.of("permeabilityForm.kvKhMin.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityForm.kvKhMax.decimalInputValue", Set.of("permeabilityForm.kvKhMax.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityForm.kvKhAverage.decimalInputValue", Set.of("permeabilityForm.kvKhAverage.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  @Test
  void validate_whenInValidForm_emptyPermeability_thenValidationErrors() {
    var form = new PermeabilityDryWellForm();
    form.setHasPermeability(true);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityDryWellFormValidator.validate(form, bindingResult, ValidationType.FULL);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .containsExactly(
            entry("permeabilityForm.permeabilityMin.decimalInputValue", Set.of("permeabilityForm.permeabilityMin.decimalInputValue.required")),
            entry("permeabilityForm.permeabilityMax.decimalInputValue", Set.of("permeabilityForm.permeabilityMax.decimalInputValue.required")),
            entry("permeabilityForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityForm.permeabilityAverage.decimalInputValue.required")),
            entry("permeabilityForm.averageMethodUsed", Set.of("permeabilityForm.averageMethodUsed.required")),
            entry("permeabilityForm.permeabilitySource", Set.of("permeabilityForm.permeabilitySource.required")),
            entry("permeabilityForm.kvKhMin.decimalInputValue", Set.of("permeabilityForm.kvKhMin.decimalInputValue.required")),
            entry("permeabilityForm.kvKhMax.decimalInputValue", Set.of("permeabilityForm.kvKhMax.decimalInputValue.required")),
            entry("permeabilityForm.kvKhAverage.decimalInputValue", Set.of("permeabilityForm.kvKhAverage.decimalInputValue.required")),
            entry("permeabilityForm.kvKhAverageMethodUsed", Set.of("permeabilityForm.kvKhAverageMethodUsed.required")),
            entry("permeabilityForm.kvKhSource", Set.of("permeabilityForm.kvKhSource.required"))
        );
  }

  @Test
  void validate_whenOtherSourceNotFilledIn_thenValidationErrors() {
    var form = getValidFormWithPermeability();

    form.getPermeabilityForm().setKvKhSource(PermeabilitySource.OTHER);
    form.getPermeabilityForm().setOtherKvKhSource("");
    form.getPermeabilityForm().setPermeabilitySource(PermeabilitySource.OTHER);
    form.getPermeabilityForm().setOtherPermeabilitySource("");
    form.getPermeabilityForm().setKvKhAverageMethodUsed(PermeabilitySource.OTHER);
    form.getPermeabilityForm().setKvKhAverageMethodUsedOther("");

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityDryWellFormValidator.validate(form, bindingResult, ValidationType.FULL);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityForm.otherPermeabilitySource", Set.of("permeabilityForm.otherPermeabilitySource.required")),
            entry("permeabilityForm.kvKhAverageMethodUsedOther", Set.of("permeabilityForm.kvKhAverageMethodUsedOther.required")),
            entry("permeabilityForm.otherKvKhSource", Set.of("permeabilityForm.otherKvKhSource.required"))
        );
  }

  static public PermeabilityDryWellForm getValidFormWithPermeability() {
    var form = new PermeabilityDryWellForm();
    var permeabilityForm = new PermeabilityForm();
    permeabilityForm.setPermeabilityComments("comments");
    permeabilityForm.setPermeabilitySource(PermeabilitySource.OTHER);
    permeabilityForm.setOtherPermeabilitySource("other source");
    permeabilityForm.setPermeabilityAverage(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityForm.setPermeabilityMin(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityForm.setPermeabilityMax(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityForm.setKvKhAverage(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityForm.setKvKhMin(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityForm.setKvKhMax(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityForm.setKvKhSource(PermeabilitySource.OTHER);
    permeabilityForm.setOtherKvKhSource("source");
    permeabilityForm.setAverageMethodUsed(AverageMethod.ARITHMETIC);
    permeabilityForm.setKvKhAverageMethodUsedOther("other source");
    permeabilityForm.setKvKhAverageMethodUsed(PermeabilitySource.OTHER);
    permeabilityForm.setKvKhComments("comments");
    form.setPermeabilityForm(permeabilityForm);
    form.setHasPermeability(true);
    return form;
  }

  private PermeabilityDryWellForm getValidFormWithoutPermeability() {
    var form = new PermeabilityDryWellForm();
    var permeabilityForm = new PermeabilityForm();
    form.setPermeabilityForm(permeabilityForm);
    form.setHasPermeability(false);
    return form;
  }
}