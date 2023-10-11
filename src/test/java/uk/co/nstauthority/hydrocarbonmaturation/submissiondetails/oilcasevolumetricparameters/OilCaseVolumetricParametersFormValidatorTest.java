package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

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
class OilCaseVolumetricParametersFormValidatorTest {

  private OilCaseVolumetricParametersFormValidator oilCaseVolumetricParametersFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    oilCaseVolumetricParametersFormValidator = new OilCaseVolumetricParametersFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new OilCaseVolumetricParametersForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("oilFormationVolumeFactor.probabilityDistributionType",
                Set.of("oilFormationVolumeFactor.probabilityDistributionType.required")),
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
      names = {"CUSTOM", "NOT_CALCULATED"}
  )
  void validate_whenEmptyExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenField_whenEmptyExceedanceForm_thenValidationErrors() {
    var form = new OilCaseVolumetricParametersForm();
    var constantDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CONSTANT,
            new ProbabilityOfExceedanceForm());
    var customDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            new ProbabilityOfExceedanceForm());
    form.setAssociatedGasOilRatioLifeOfField(constantDistributionForm);
    form.setOilRecoveryFactor(constantDistributionForm);
    form.setOilFormationVolumeFactor(constantDistributionForm);
    form.setOilHydrocarbonSaturation(customDistributionForm);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("oilFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("oilRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error"))
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
    oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("oilFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilFormationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenField_whenInvalidExceedanceForm_thenValidationErrors() {
    var form = new OilCaseVolumetricParametersForm();
    var constantDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CONSTANT,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    var customDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setAssociatedGasOilRatioLifeOfField(constantDistributionForm);
    form.setOilRecoveryFactor(constantDistributionForm);
    form.setOilFormationVolumeFactor(constantDistributionForm);
    form.setOilHydrocarbonSaturation(customDistributionForm);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("oilFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("oilFormationVolumeFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("oilRecoveryFactor." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error"))
        );
  }

  private OilCaseVolumetricParametersForm getFormWithInvalidDistributions(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = new OilCaseVolumetricParametersForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setOilFormationVolumeFactor(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);

    return form;
  }

  private OilCaseVolumetricParametersForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new OilCaseVolumetricParametersForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setOilFormationVolumeFactor(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);
    return form;
  }

  private OilCaseVolumetricParametersForm getValidForm() {
    var form = new OilCaseVolumetricParametersForm();
    var probDistributionForm = ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();
    form.setOilHydrocarbonSaturation(probDistributionForm);
    form.setOilRecoveryFactor(probDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probDistributionForm);
    form.setOilFormationVolumeFactor(probDistributionForm);
    return form;
  }

}