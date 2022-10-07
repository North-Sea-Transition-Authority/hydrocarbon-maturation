package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

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
class OilCaseVolumetricsFormValidatorTest {

  private OilCaseVolumetricsFormValidator oilCaseVolumetricsFormValidator;

  @BeforeEach
  void setUp() {
    oilCaseVolumetricsFormValidator = new OilCaseVolumetricsFormValidator();
  }

  @Test
  void supports_whenOilCaseVolumetricsFormClass_thenTrue() {
    var supportedClass = OilCaseVolumetricsForm.class;
    assertTrue(oilCaseVolumetricsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotOilCaseVolumetricsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(oilCaseVolumetricsFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseVolumetricsFormValidator.validate(form, bindingResult, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new OilCaseVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseVolumetricsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("formationVolumeFactor.probabilityDistributionType",
                Set.of("formationVolumeFactor.probabilityDistributionType.required")),
            entry("oilHydrocarbonSaturation.probabilityDistributionType",
                Set.of("oilHydrocarbonSaturation.probabilityDistributionType.required")),
            entry("associatedGasOilRatioLifeOfField.probabilityDistributionType",
                Set.of("associatedGasOilRatioLifeOfField.probabilityDistributionType.required")),
            entry("oilRecoveryFactor.probabilityDistributionType",
                Set.of("oilRecoveryFactor.probabilityDistributionType.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = "NOT_CALCULATED"
  )
  void validate_whenEmptyExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseVolumetricsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = "NOT_CALCULATED"
  )
  void validate_whenInvalidExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseVolumetricsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  private OilCaseVolumetricsForm getFormWithInvalidDistributions(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = new OilCaseVolumetricsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setFormationVolumeFactor(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);

    return form;
  }

  private OilCaseVolumetricsForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new OilCaseVolumetricsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setFormationVolumeFactor(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);
    return form;
  }

  private OilCaseVolumetricsForm getValidForm() {
    var form = new OilCaseVolumetricsForm();
    var probDistributionForm = ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();
    form.setOilHydrocarbonSaturation(probDistributionForm);
    form.setOilRecoveryFactor(probDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probDistributionForm);
    form.setFormationVolumeFactor(probDistributionForm);
    return form;
  }

}