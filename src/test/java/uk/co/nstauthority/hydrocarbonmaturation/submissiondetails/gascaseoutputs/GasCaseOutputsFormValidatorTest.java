package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
class GasCaseOutputsFormValidatorTest {

  private GasCaseOutputsFormValidator gasCaseOutputsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    gasCaseOutputsFormValidator = new GasCaseOutputsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenGasCaseOutputsFormClass_thenTrue() {
    var supportedClass = GasCaseOutputsForm.class;
    assertTrue(gasCaseOutputsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotGasCaseOutputsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(gasCaseOutputsFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenProspect_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenDiscovery_thenNoValidationErrors(ValidationType validationType) {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, discoverySubmissionDetail));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_whenProspect_thenValidationErrors() {
    var form = getFormWithEmptyDistributions(ProbabilityDistributionType.CUSTOM);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenInvalidForm_whenDiscovery_thenValidationErrors() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = getFormWithEmptyDistributions(ProbabilityDistributionType.CUSTOM);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, discoverySubmissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenYesNoNotSelected_thenValidationErrors() {
    var form = getValidForm();
    form.setHasAssociatedCondensateRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("hasAssociatedCondensateRecoverableResources", Set.of("hasAssociatedCondensateRecoverableResources.required"))
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
    gasCaseOutputsFormValidator.validate(form, bindingResult,
        new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
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
    gasCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  private GasCaseOutputsForm getFormWithInvalidDistributions(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = new GasCaseOutputsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm()
        );
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setGasInPlace(probabilityDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);

    return form;
  }

  private GasCaseOutputsForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = new GasCaseOutputsForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setGasInPlace(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);
    return form;
  }

  private GasCaseOutputsForm getValidForm() {
    var form = new GasCaseOutputsForm();
    var probDistributionForm = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    form.setGasInPlace(probDistributionForm);
    form.setOilEquivalentRecoverableResources(probDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probDistributionForm);
    form.setGasRecoverableResources(probDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);
    return form;
  }
}