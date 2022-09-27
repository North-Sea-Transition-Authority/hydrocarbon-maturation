package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricsFormValidatorTest {

  private GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator;

  @BeforeEach
  void setUp() {
    gasCaseVolumetricsFormValidator = new GasCaseVolumetricsFormValidator();
  }

  @Test
  void supports_whenGasCaseVolumetricsFormClass_thenTrue() {
    var supportedClass = GasCaseVolumetricsForm.class;
    assertTrue(gasCaseVolumetricsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotGasCaseVolumetricsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(gasCaseVolumetricsFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource (value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricsFormValidator.validate(form, bindingResult, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new GasCaseVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("gasExpansionFactor.probabilityDistributionType",
                Set.of("gasExpansionFactor.probabilityDistributionType.required")),
            entry("gasRecoveryFactor.probabilityDistributionType",
                Set.of("gasRecoveryFactor.probabilityDistributionType.required")),
            entry("gasHydrocarbonSaturation.probabilityDistributionType",
                Set.of("gasHydrocarbonSaturation.probabilityDistributionType.required")),
            entry("condensateGasRatioLifeOfField.probabilityDistributionType",
                Set.of("condensateGasRatioLifeOfField.probabilityDistributionType.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class
  )
  void validate_whenEmptyExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class
  )
  void validate_whenInvalidExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  private GasCaseVolumetricsForm getFormWithInvalidDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new GasCaseVolumetricsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setGasExpansionFactor(probabilityDistributionForm);

    return form;
  }

  private GasCaseVolumetricsForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new GasCaseVolumetricsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setGasExpansionFactor(probabilityDistributionForm);
    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    return form;
  }

  private GasCaseVolumetricsForm getValidForm() {
    var form = new GasCaseVolumetricsForm();
    form.setGasRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setCondensateGasRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasExpansionFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    return form;
  }
}