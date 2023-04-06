package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PermeabilityFormValidatorTest {

  @InjectMocks
  private PermeabilityFormValidator permeabilityFormValidator;

  static private SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

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
    permeabilityFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(
            validationType,
            submissionDetail));

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withoutPermeability_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithoutPermeability();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(
            validationType,
            submissionDetail));

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

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(
            validationType,
            submissionDetail));
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.incorrectDecimalPlaces")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenGivenForm_withLessThanMinValue_thenValidationErrors(ValidationType validationType) {
    var form = getValidFormWithPermeability();
    form.getPermeabilityValuesForm().setPermeabilityMax(DecimalInput.createDecimalInput(BigDecimal.valueOf(-5)));
    form.getPermeabilityValuesForm().setPermeabilityMin(DecimalInput.createDecimalInput(BigDecimal.valueOf(-3)));
    form.getPermeabilityValuesForm().setPermeabilityAverage(DecimalInput.createDecimalInput(BigDecimal.valueOf(-1)));
    form.getPermeabilityValuesForm().setKvKhAverage(DecimalInput.createDecimalInput(BigDecimal.valueOf(-6)));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(
            validationType,
            submissionDetail));
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.lessThanMinValue")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.lessThanMinValue")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.lessThanMinValue")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.lessThanMinValue"))
        );
  }

  @Test
  void validate_whenInValidForm_emptyPermeability_thenValidationErrors() {
    var form = new PermeabilityForm();
    form.setHasPermeability(true);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(
            ValidationType.FULL,
            submissionDetail));
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.averageMethodUsed", Set.of("permeabilityValuesForm.averageMethodUsed.required")),
            entry("permeabilityValuesForm.permeabilitySource", Set.of("permeabilityValuesForm.permeabilitySource.required")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.required"))
        );
  }

  @Test
  void validate_whenInValidForm_emptyPermeability_discoverySubmission_thenValidationErrors() {
    var form = new PermeabilityForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(
            ValidationType.FULL,
            submissionDetail));
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.averageMethodUsed", Set.of("permeabilityValuesForm.averageMethodUsed.required")),
            entry("permeabilityValuesForm.permeabilitySource", Set.of("permeabilityValuesForm.permeabilitySource.required")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.required"))
        );
  }

  @Test
  void validate_whenOtherSourceNotFilledIn_thenValidationErrors() {
    var form = getValidFormWithPermeability();

    form.getPermeabilityValuesForm().setPermeabilitySource(PermeabilitySource.OTHER);
    form.getPermeabilityValuesForm().setOtherPermeabilitySource("");

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail)
    );
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.otherPermeabilitySource", Set.of("permeabilityValuesForm.otherPermeabilitySource.required"))
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
    permeabilityForm.setAverageMethodUsed(AverageMethod.ARITHMETIC);
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