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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
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
class OilCaseOutputsFormValidatorTest {

  private OilCaseOutputsFormValidator oilCaseOutputsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    oilCaseOutputsFormValidator = new OilCaseOutputsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
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
  void validate_whenValidForm_andProspect_thenNoValidationErrors(ValidationType validationType) {
    var form = getProspectValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_andDiscovery_thenNoValidationErrors(ValidationType validationType) {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = getDiscoveryValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, discoverySubmissionDetail));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_andProspect_thenValidationErrors() {
    var form = getFormWithEmptyDistributions(ProbabilityDistributionType.CUSTOM);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

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
  void validate_whenInvalidForm_andDiscovery_thenValidationErrors() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = getFormWithEmptyDistributions(ProbabilityDistributionType.CUSTOM);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, discoverySubmissionDetail));

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
    var form = getProspectValidForm();
    form.setHasAssociatedGasRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

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
    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

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
    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

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
    var form = getProspectValidForm();
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput(""));

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOilCaseOutputsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("riskedOilEquivalentRecoverableMean.decimalInputValue", Set.of("riskedOilEquivalentRecoverableMean.decimalInputValue.required"))
        );
  }

  private BindingResult validateOilCaseOutputsForm_fullValidation(OilCaseOutputsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    oilCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

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

  private OilCaseOutputsForm getProspectValidForm() {
    return getValidForm(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT);
  }

  private OilCaseOutputsForm getDiscoveryValidForm() {
    return getValidForm(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
  }

  private OilCaseOutputsForm getValidForm(
      ProbabilityDistributionFieldInstance probDistributionFieldInstanceForRecoverableResource) {
    var form = new OilCaseOutputsForm();
    var probDistributionForm = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    var probDistributionFormForRecoverableResource = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        probDistributionFieldInstanceForRecoverableResource
    );
    form.setOilInPlace(probDistributionForm);
    form.setAssociatedGasRecoverableResources(probDistributionFormForRecoverableResource);
    form.setOilEquivalentRecoverableResources(probDistributionFormForRecoverableResource);
    form.setOilRecoverableResources(probDistributionFormForRecoverableResource);
    form.setRiskedOilEquivalentRecoverableMean(new DecimalInput("1"));
    form.setHasAssociatedGasRecoverableResources(true);
    return form;
  }
}