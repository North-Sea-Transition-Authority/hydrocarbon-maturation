package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryMixedCaseVolumetricsFormValidatorTest {

  private DiscoveryMixedCaseVolumetricsFormValidator discoveryMixedCaseVolumetricsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    discoveryMixedCaseVolumetricsFormValidator = new DiscoveryMixedCaseVolumetricsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryMixedCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenUseIndustryStandardConversion_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    form.setUseIndustryStandardConversion(true);
    form.setOilEquivalentRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryMixedCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var probabilityDistributionType = ProbabilityDistributionType.CUSTOM;
    var form = getFormWithInvalidDistributions();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryMixedCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
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
            entry("gasInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenYesNoNotSelected_thenValidationErrors() {
    var form = getValidForm();
    form.setHasAssociatedCondensateRecoverableResources(null);
    form.setUseIndustryStandardConversion(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryMixedCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("hasAssociatedCondensateRecoverableResources", Set.of("hasAssociatedCondensateRecoverableResources.required")),
            entry("useIndustryStandardConversion", Set.of("useIndustryStandardConversion.required"))
        );
  }

  @Test
  void validate_whenEmptyExceedanceForm_thenValidationErrors() {
    var probabilityDistributionType = ProbabilityDistributionType.CUSTOM;
    var form = getFormWithEmptyDistributions();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryMixedCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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
            entry("gasInPlace." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasInPlace." + probabilityDistributionType.getPath() + ".error.error")),
            entry("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("gasRecoverableResources." + probabilityDistributionType.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  private MixedCaseOutputsForm getFormWithEmptyDistributions() {
    var form = new MixedCaseOutputsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            new ProbabilityOfExceedanceForm());
    form.setGasInPlace(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setOilInPlace(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setUseIndustryStandardConversion(false);
    return form;
  }

  private MixedCaseOutputsForm getFormWithInvalidDistributions() {
    var form = new MixedCaseOutputsForm();

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            ProbabilityDistributionType.CUSTOM,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm()
        );
    form.setGasInPlace(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setOilInPlace(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setUseIndustryStandardConversion(false);
    return form;
  }

  private MixedCaseOutputsForm getValidForm() {
    var form = new MixedCaseOutputsForm();
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    var oilRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedGas = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    );
    var gasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedCondensate = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var oilEquivalent = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
    form.setOilInPlace(oilInPlace);
    form.setOilRecoverableResources(oilRecoverableResources);
    form.setAssociatedGasRecoverableResources(associatedGas);
    form.setGasInPlace(gasInPlace);
    form.setOilEquivalentRecoverableResources(oilEquivalent);
    form.setAssociatedCondensateRecoverableResources(associatedCondensate);
    form.setGasRecoverableResources(gasRecoverableResources);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setUseIndustryStandardConversion(false);
    return form;
  }

}