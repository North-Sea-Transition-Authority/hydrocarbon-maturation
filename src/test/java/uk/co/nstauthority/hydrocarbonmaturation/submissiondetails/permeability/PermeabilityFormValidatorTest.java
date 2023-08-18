package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PermeabilityFormValidatorTest {

  @InjectMocks
  private PermeabilityFormValidator permeabilityFormValidator;

  static private SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withPermeability_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithPermeability();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var isValid = permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);

    assertTrue(isValid);
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_partial_whenValidForm_fieldSubmission_withPermeabilityAndProbabilityDistributions_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithPermeabilityAndProbabilityDistributions();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var isValid = permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);

    assertTrue(isValid);
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withoutPermeability_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidFormWithoutPermeability();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var isValid = permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);

    assertTrue(isValid);
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

    permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);

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
    permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);

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
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL);
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
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL);
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
  void validate_whenInValidForm_emptyPermeability_fieldSubmission_thenValidationErrors() {
    var form = new PermeabilityForm();
    form.setPermeabilityValuesForm(getValuesFormWithEmptyDistributions());
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .contains(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.averageMethodUsed", Set.of("permeabilityValuesForm.averageMethodUsed.required")),
            entry("permeabilityValuesForm.permeabilitySource", Set.of("permeabilityValuesForm.permeabilitySource.required")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("permeabilityValuesForm.permeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("permeabilityValuesForm.horizontalPermeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("permeabilityValuesForm.horizontalPermeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("permeabilityValuesForm.verticalPermeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("permeabilityValuesForm.verticalPermeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenInValidForm_notEmptyPermeability_fieldSubmission_thenValidationErrors() {
    var form = getEmptyFormWithPermeabilityAndProbabilityDistributions();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors)
        .contains(
            entry("permeabilityValuesForm.permeabilityMin.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMin.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityMax.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityMax.decimalInputValue.required")),
            entry("permeabilityValuesForm.permeabilityAverage.decimalInputValue", Set.of("permeabilityValuesForm.permeabilityAverage.decimalInputValue.required")),
            entry("permeabilityValuesForm.averageMethodUsed", Set.of("permeabilityValuesForm.averageMethodUsed.required")),
            entry("permeabilityValuesForm.permeabilitySource", Set.of("permeabilityValuesForm.permeabilitySource.required")),
            entry("permeabilityValuesForm.kvKhAverage.decimalInputValue", Set.of("permeabilityValuesForm.kvKhAverage.decimalInputValue.required"))
        ).doesNotContainKeys(
            "permeabilityValuesForm.permeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
            "permeabilityValuesForm.horizontalPermeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
            "permeabilityValuesForm.verticalPermeability." + ProbabilityDistributionType.CUSTOM.getPath() + ".error"
        );
  }

  @Test
  void validate_whenOtherSourceNotFilledIn_thenValidationErrors() {
    var form = getValidFormWithPermeability();

    form.getPermeabilityValuesForm().setPermeabilitySource(PermeabilitySource.OTHER);
    form.getPermeabilityValuesForm().setOtherPermeabilitySource("");

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    permeabilityFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL);
    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("permeabilityValuesForm.otherPermeabilitySource", Set.of("permeabilityValuesForm.otherPermeabilitySource.required"))
        );
  }

  static public PermeabilityForm getValidFormWithPermeability() {
    var form = new PermeabilityForm();
    var permeabilityValuesForm = new PermeabilityValuesForm();
    permeabilityValuesForm.setPermeabilityComments("comments");
    permeabilityValuesForm.setPermeabilitySource(PermeabilitySource.OTHER);
    permeabilityValuesForm.setOtherPermeabilitySource("other source");
    permeabilityValuesForm.setPermeabilityAverage(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityValuesForm.setPermeabilityMin(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityValuesForm.setPermeabilityMax(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityValuesForm.setKvKhAverage(DecimalInput.createDecimalInput(BigDecimal.ONE));
    permeabilityValuesForm.setAverageMethodUsed(AverageMethod.ARITHMETIC);
    form.setPermeabilityValuesForm(permeabilityValuesForm);
    form.setHasPermeability(true);
    return form;
  }

  static public PermeabilityForm getValidFormWithPermeabilityAndProbabilityDistributions() {
    var form = getValidFormWithPermeability();
    var permeabilityValuesForm = getValuesFormWithDistributions(form.getPermeabilityValuesForm());
    form.setPermeabilityValuesForm(permeabilityValuesForm);
    return form;
  }

  static public PermeabilityForm getEmptyFormWithPermeabilityAndProbabilityDistributions() {
    var form = new PermeabilityForm();
    var permeabilityValuesForm = getValuesFormWithDistributions(form.getPermeabilityValuesForm());
    form.setPermeabilityValuesForm(permeabilityValuesForm);
    return form;
  }

  private PermeabilityForm getValidFormWithoutPermeability() {
    var form = new PermeabilityForm();
    var permeabilityForm = new PermeabilityValuesForm();
    form.setPermeabilityValuesForm(permeabilityForm);
    form.setHasPermeability(false);
    return form;
  }

  private PermeabilityValuesForm getValuesFormWithEmptyDistributions() {
    var form = new PermeabilityValuesForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            new ProbabilityOfExceedanceForm());
    form.setPermeability(probabilityDistributionForm);
    form.setHorizontalPermeability(probabilityDistributionForm);
    form.setVerticalPermeability(probabilityDistributionForm);
    return form;
  }

  private static PermeabilityValuesForm getValuesFormWithDistributions(PermeabilityValuesForm form) {
    form.setPermeability(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY));
    form.setHorizontalPermeability(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY));
    form.setVerticalPermeability(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY));
    return form;
  }
}