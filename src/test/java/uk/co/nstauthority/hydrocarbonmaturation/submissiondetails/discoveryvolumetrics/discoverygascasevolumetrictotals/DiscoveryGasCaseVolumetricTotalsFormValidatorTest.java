package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryGasCaseVolumetricTotalsFormValidatorTest {

  private DiscoveryGasCaseVolumetricTotalsFormValidator discoveryGasCaseVolumetricTotalsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    discoveryGasCaseVolumetricTotalsFormValidator = new DiscoveryGasCaseVolumetricTotalsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getDiscoveryValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_whenUseIndustryStandardConversion_thenNoValidationErrors(ValidationType validationType) {
    var form = getDiscoveryValidForm();
    form.setUseIndustryStandardConversion(true);
    form.setOilEquivalentRecoverableResources(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = getFormWithEmptyDistributions(ProbabilityDistributionType.CUSTOM);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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
    var form = getDiscoveryValidForm();
    form.setHasAssociatedCondensateRecoverableResources(null);
    form.setUseIndustryStandardConversion(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("hasAssociatedCondensateRecoverableResources", Set.of("hasAssociatedCondensateRecoverableResources.required")),
            entry("useIndustryStandardConversion", Set.of("useIndustryStandardConversion.required"))
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
    discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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
    discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);

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

  private GasCaseOutputsForm getDiscoveryValidForm() {
    return getValidForm();
  }

  private GasCaseOutputsForm getValidForm() {
    var form = new GasCaseOutputsForm();
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY
    );
    var recoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var associatedCondensate = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    );
    var oilEquivalent = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );
    form.setGasInPlace(gasInPlace);
    form.setOilEquivalentRecoverableResources(oilEquivalent);
    form.setAssociatedCondensateRecoverableResources(associatedCondensate);
    form.setGasRecoverableResources(recoverableResources);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setUseIndustryStandardConversion(false);
    return form;
  }
}
