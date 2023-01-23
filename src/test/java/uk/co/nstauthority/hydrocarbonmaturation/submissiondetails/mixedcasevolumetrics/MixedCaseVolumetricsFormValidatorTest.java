package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

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
class MixedCaseVolumetricsFormValidatorTest {

  private MixedCaseVolumetricsFormValidator mixedCaseVolumetricsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    mixedCaseVolumetricsFormValidator = new MixedCaseVolumetricsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenMixedCaseVolumetricsFormClass_thenTrue() {
    var supportedClass = MixedCaseVolumetricsForm.class;
    assertTrue(mixedCaseVolumetricsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotMixedCaseVolumetricsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(mixedCaseVolumetricsFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource (value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    mixedCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new MixedCaseVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    mixedCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

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
                Set.of("oilRecoveryFactor.probabilityDistributionType.required")),
            entry("gasHydrocarbonSaturation.probabilityDistributionType",
                Set.of("gasHydrocarbonSaturation.probabilityDistributionType.required")),
            entry("gasExpansionFactor.probabilityDistributionType",
                Set.of("gasExpansionFactor.probabilityDistributionType.required")),
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
    mixedCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
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
    mixedCaseVolumetricsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("formationVolumeFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasHydrocarbonSaturation." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedGasOilRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasExpansionFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error",
                Set.of("condensateGasRatioLifeOfField." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoveryFactor." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  private MixedCaseVolumetricsForm getFormWithInvalidDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new MixedCaseVolumetricsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());

    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    form.setGasExpansionFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);
    form.setFormationVolumeFactor(probabilityDistributionForm);
    return form;
  }

  private MixedCaseVolumetricsForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new MixedCaseVolumetricsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());

    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    form.setGasExpansionFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);
    form.setFormationVolumeFactor(probabilityDistributionForm);
    return form;
  }

  private MixedCaseVolumetricsForm getValidForm() {
    var form = new MixedCaseVolumetricsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    form.setGasHydrocarbonSaturation(probabilityDistributionForm);
    form.setOilRecoveryFactor(probabilityDistributionForm);
    form.setCondensateGasRatioLifeOfField(probabilityDistributionForm);
    form.setGasExpansionFactor(probabilityDistributionForm);
    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionForm);
    form.setGasRecoveryFactor(probabilityDistributionForm);
    form.setOilHydrocarbonSaturation(probabilityDistributionForm);
    form.setFormationVolumeFactor(probabilityDistributionForm);
    return form;
  }
}