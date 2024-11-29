package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
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

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenProspect_thenNoValidationErrors(ValidationType validationType) {
    var form = getProspectValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenProspect_whenUseIndustryStandard_thenNoValidationErrors(ValidationType validationType) {
    var form = getProspectValidForm();
    form.setUseIndustryStandardConversion(true);
    form.setOilEquivalentRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenDiscovery_thenNoValidationErrors(ValidationType validationType) {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = getDiscoveryValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.isValid(form, bindingResult, discoverySubmissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenDiscovery_whenUseIndustryStandardConversion_thenNoValidationErrors(ValidationType validationType) {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = getDiscoveryValidForm();
    form.setUseIndustryStandardConversion(true);
    form.setOilEquivalentRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.isValid(form, bindingResult, discoverySubmissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_whenProspect_thenValidationErrors() {
    var form = getFormWithEmptyDistributions(ProbabilityDistributionType.CUSTOM);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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
    gasCaseOutputsFormValidator.isValid(form, bindingResult, discoverySubmissionDetail, ValidationType.FULL);

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
    var form = getProspectValidForm();
    form.setHasAssociatedCondensateRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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
    gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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
    gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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
    form.setUseIndustryStandardConversion(false);

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
    form.setUseIndustryStandardConversion(false);
    return form;
  }

  private GasCaseOutputsForm getProspectValidForm() {
    return getValidForm(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT);
  }

  private GasCaseOutputsForm getDiscoveryValidForm() {
    return getValidForm(ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
  }

  private GasCaseOutputsForm getValidForm(ProbabilityDistributionFieldInstance probDistributionFieldInstanceForRecoverableResource) {
    var form = new GasCaseOutputsForm();
    var probDistributionForm = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    var probDistributionFormForRecoverableResource = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        probDistributionFieldInstanceForRecoverableResource
    );
    form.setGasInPlace(probDistributionForm);
    form.setOilEquivalentRecoverableResources(probDistributionFormForRecoverableResource);
    form.setAssociatedCondensateRecoverableResources(probDistributionFormForRecoverableResource);
    form.setGasRecoverableResources(probDistributionFormForRecoverableResource);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setUseIndustryStandardConversion(false);
    return form;
  }
}