package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsFormValidatorTest {

  private OilCaseOutputsFormValidator oilCaseOutputsFormValidator;

  @BeforeEach
  void setUp() {
    oilCaseOutputsFormValidator = new OilCaseOutputsFormValidator();
  }

  @Test
  void supports_whenOilCaseOutputsFormClass_thenTrue() {
    var supportedClass = OilCaseOutputsForm.class;
    assertTrue(oilCaseOutputsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotOilCaseOutputsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(oilCaseOutputsFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(form, bindingResult, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = getFormWithEmptyDistributions(ProbabilityDistributionType.CUSTOM);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenYesNoNotSelected_thenValidationErrors() {
    var form = getValidForm();
    form.setHasAssociatedGasRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("hasAssociatedGasRecoverableResources", Set.of("hasAssociatedGasRecoverableResources.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      names = {"CUSTOM", "NOT_CALCULATED"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void validate_whenEmptyExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      names = {"CUSTOM", "NOT_CALCULATED"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void validate_whenInvalidExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("riskedOilEquivalentRecoverableMean.decimalInputValue", Set.of("riskedOilEquivalentRecoverableMean.decimalInputValue.notNumeric"))
        );
  }

  @Test
  void validate_whenInValidForm_noRiskedOilEquivalentRecoverableMean_thenValidationErrors() {
    var form = getValidForm();
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput(""));

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOilCaseOutputsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("riskedOilEquivalentRecoverableMean.decimalInputValue", Set.of("riskedOilEquivalentRecoverableMean.decimalInputValue.required"))
        );
  }

  private BindingResult validateOilCaseOutputsForm_fullValidation(OilCaseOutputsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    oilCaseOutputsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    return bindingResult;
  }

  private OilCaseOutputsForm getFormWithInvalidDistributions(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = new OilCaseOutputsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm()
        );
    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setOilInPlace(probabilityDistributionForm);
    form.setHasAssociatedGasRecoverableResources(true);
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput("abc"));

    return form;
  }

  private OilCaseOutputsForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new OilCaseOutputsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setOilInPlace(probabilityDistributionForm);
    form.setHasAssociatedGasRecoverableResources(true);
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput(""));
    return form;
  }

  private OilCaseOutputsForm getValidForm() {
    var form = new OilCaseOutputsForm();
    var probDistributionForm = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setOilInPlace(probDistributionForm);
    form.setAssociatedGasRecoverableResources(probDistributionForm);
    form.setOilEquivalentRecoverableResources(probDistributionForm);
    form.setOilRecoverableResources(probDistributionForm);
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput("1"));
    form.setHasAssociatedGasRecoverableResources(true);
    return form;
  }
}