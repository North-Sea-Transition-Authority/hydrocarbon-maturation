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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricsFormValidatorTest {

  private GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    gasCaseVolumetricsFormValidator = new GasCaseVolumetricsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
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
    gasCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new GasCaseVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("gasHydrocarbonSaturation.probabilityDistributionType",
                Set.of("gasHydrocarbonSaturation.probabilityDistributionType.required")),
            entry("condensateGasRatioLifeOfField.probabilityDistributionType",
                Set.of("condensateGasRatioLifeOfField.probabilityDistributionType.required")),
            entry("gasRecoveryFactor.probabilityDistributionType",
                Set.of("gasRecoveryFactor.probabilityDistributionType.required")),
            entry("gasFormationVolumeFactor.probabilityDistributionType",
                Set.of("gasFormationVolumeFactor.probabilityDistributionType.required"))
            );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"}
  )
  void validate_whenEmptyExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"}
  )
  void validate_whenInvalidExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error"))
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
    form.setGasFormationVolumeFactor(probabilityDistributionForm);

    return form;
  }

  private GasCaseVolumetricsForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new GasCaseVolumetricsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());

    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    form.setGasFormationVolumeFactor(probabilityDistributionForm);

    return form;
  }

  private GasCaseVolumetricsForm getValidForm() {
    var form = new GasCaseVolumetricsForm();

    form.setGasRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setCondensateGasRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    return form;
  }
}