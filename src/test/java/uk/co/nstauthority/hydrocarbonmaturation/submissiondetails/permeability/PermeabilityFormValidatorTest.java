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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PermeabilityFormValidatorTest {

  @InjectMocks
  private PermeabilityFormValidator permeabilityFormValidator;

  @Test
  void supports_whenPermeabilityDryWellFormClass_thenTrue() {
    var supportedClass = PermeabilityForm.class;
    assertTrue(permeabilityFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenPermeabilityDryWellFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(permeabilityFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withPermeability_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithPermeability();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(form, bindingResult, validationType, SubmissionType.CREATE_DISCOVERY);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withoutPermeability_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithoutPermeability();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(form, bindingResult, validationType, SubmissionType.POST_DRILL_DRY_HOLE);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenGivenForm_withIncorrectDecimalPlaces_thenValidationErrors(ValidationType validationType) {
    var form = getValidFormWithPermeability();
    form.getPermeabilityValuesForm().setPermeabilityMax(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityValuesForm().setPermeabilityMin(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityValuesForm().setPermeabilityAverage(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityValuesForm().setKvKhAverage(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityValuesForm().setKvKhMax(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));
    form.getPermeabilityValuesForm().setKvKhMin(DecimalInput.createDecimalInput(BigDecimal.valueOf(5.5555)));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(form, bindingResult, validationType, SubmissionType.POST_DRILL_DRY_HOLE);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.kvKhMin.decimalInputValue", Set.of("permeabilityValuesForm.kvKhMin.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.kvKhMax.decimalInputValue", Set.of("permeabilityValuesForm.kvKhMax.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  @Test
  void validate_whenInValidForm_emptyPermeability_thenValidationErrors() {
    var form = new PermeabilityForm();
    form.setHasPermeability(true);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(form, bindingResult, ValidationType.FULL, SubmissionType.POST_DRILL_DRY_HOLE);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.averageMethodUsed", Set.of("permeabilityValuesForm.averageMethodUsed.required")),
            entry("permeabilityValuesForm.permeabilitySource", Set.of("permeabilityValuesForm.permeabilitySource.required")),
            entry("permeabilityValuesForm.kvKhMin.decimalInputValue", Set.of("permeabilityValuesForm.kvKhMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.kvKhMax.decimalInputValue", Set.of("permeabilityValuesForm.kvKhMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.kvKhAverageMethodUsed", Set.of("permeabilityValuesForm.kvKhAverageMethodUsed.required")),
            entry("permeabilityValuesForm.kvKhSource", Set.of("permeabilityValuesForm.kvKhSource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_emptyPermeability_discoverySubmission_thenValidationErrors() {
    var form = new PermeabilityForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(form, bindingResult, ValidationType.FULL, SubmissionType.CREATE_DISCOVERY);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.averageMethodUsed", Set.of("permeabilityValuesForm.averageMethodUsed.required")),
            entry("permeabilityValuesForm.permeabilitySource", Set.of("permeabilityValuesForm.permeabilitySource.required")),
            entry("permeabilityValuesForm.kvKhMin.decimalInputValue", Set.of("permeabilityValuesForm.kvKhMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.kvKhMax.decimalInputValue", Set.of("permeabilityValuesForm.kvKhMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.kvKhAverageMethodUsed", Set.of("permeabilityValuesForm.kvKhAverageMethodUsed.required")),
            entry("permeabilityValuesForm.kvKhSource", Set.of("permeabilityValuesForm.kvKhSource.required"))
        );
  }

  @Test
  void validate_whenOtherSourceNotFilledIn_thenValidationErrors() {
    var form = getValidFormWithPermeability();

    form.getPermeabilityValuesForm().setKvKhSource(PermeabilitySource.OTHER);
    form.getPermeabilityValuesForm().setOtherKvKhSource("");
    form.getPermeabilityValuesForm().setPermeabilitySource(PermeabilitySource.OTHER);
    form.getPermeabilityValuesForm().setOtherPermeabilitySource("");
    form.getPermeabilityValuesForm().setKvKhAverageMethodUsed(AverageMethod.ARITHMETIC);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(form, bindingResult, ValidationType.FULL, SubmissionType.POST_DRILL_DRY_HOLE);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.otherPermeabilitySource", Set.of("permeabilityValuesForm.otherPermeabilitySource.required")),
            entry("permeabilityValuesForm.otherKvKhSource", Set.of("permeabilityValuesForm.otherKvKhSource.required"))
        );
  }

  static public PermeabilityForm getValidFormWithPermeability() {
    var form = new PermeabilityForm();
    var permeabilityForm = new PermeabilityValuesForm();
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
    permeabilityForm.setKvKhAverageMethodUsed(AverageMethod.ARITHMETIC);
    permeabilityForm.setKvKhComments("comments");
    form.setPermeabilityValuesForm(permeabilityForm);
    form.setHasPermeability(true);
    return form;
  }

  private PermeabilityForm getValidFormWithoutPermeability() {
    var form = new PermeabilityForm();
    var permeabilityForm = new PermeabilityValuesForm();
    form.setPermeabilityValuesForm(permeabilityForm);
    form.setHasPermeability(false);
    return form;
  }
}