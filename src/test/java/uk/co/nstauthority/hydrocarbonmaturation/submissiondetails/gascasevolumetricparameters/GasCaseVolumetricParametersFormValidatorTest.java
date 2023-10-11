package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricParametersFormValidatorTest {

  private GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    gasCaseVolumetricParametersFormValidator = new GasCaseVolumetricParametersFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource (value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new GasCaseVolumetricParametersForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("gasFormationVolumeFactor.probabilityDistributionType",
                Set.of("gasFormationVolumeFactor.probabilityDistributionType.required")),
            entry("gasHydrocarbonSaturation.probabilityDistributionType",
                Set.of("gasHydrocarbonSaturation.probabilityDistributionType.required")),
            entry("condensateGasRatioLifeOfField.probabilityDistributionType",
                Set.of("condensateGasRatioLifeOfField.probabilityDistributionType.required")),
            entry("gasRecoveryFactor.probabilityDistributionType",
                Set.of("gasRecoveryFactor.probabilityDistributionType.required"))
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
    gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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

  @Test
  void validate_whenField_whenEmptyExceedanceForm_thenValidationErrors() {
    var form = new GasCaseVolumetricParametersForm();
    var constantDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CONSTANT,
            new ProbabilityOfExceedanceForm());
    var customDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            new ProbabilityOfExceedanceForm());
    form.setCondensateGasRatioLifeOfField(constantDistributionForm);
    form.setGasRecoveryFactor(constantDistributionForm);
    form.setGasFormationVolumeFactor(constantDistributionForm);
    form.setGasHydrocarbonSaturation(customDistributionForm);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("gasFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("gasHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("gasRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error"))
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
    gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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

  @Test
  void validate_whenField_whenInvalidExceedanceForm_thenValidationErrors() {
    var form = new GasCaseVolumetricParametersForm();
    var constantDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CONSTANT,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    var customDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setCondensateGasRatioLifeOfField(constantDistributionForm);
    form.setGasRecoveryFactor(constantDistributionForm);
    form.setGasFormationVolumeFactor(constantDistributionForm);
    form.setGasHydrocarbonSaturation(customDistributionForm);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("gasFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("gasFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("gasHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("gasRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error"))
        );
  }

  private GasCaseVolumetricParametersForm getFormWithInvalidDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new GasCaseVolumetricParametersForm();

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

  private GasCaseVolumetricParametersForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new GasCaseVolumetricParametersForm();

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

  private GasCaseVolumetricParametersForm getValidForm() {
    var form = new GasCaseVolumetricParametersForm();

    form.setGasRecoveryFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasHydrocarbonSaturation(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setCondensateGasRatioLifeOfField(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setGasFormationVolumeFactor(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    return form;
  }
}