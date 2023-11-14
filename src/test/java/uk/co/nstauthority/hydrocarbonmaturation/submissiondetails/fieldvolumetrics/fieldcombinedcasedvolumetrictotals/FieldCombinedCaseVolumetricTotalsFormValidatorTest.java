package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldCombinedCaseVolumetricTotalsFormValidatorTest {

  private FieldCombinedCaseVolumetricTotalsFormValidator fieldCombinedCaseVolumetricTotalsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    fieldCombinedCaseVolumetricTotalsFormValidator = new FieldCombinedCaseVolumetricTotalsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_isCsrField_invalidForm_thenErrors(ValidationType validationType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getInvalidForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        )
        .doesNotContain(
            entry("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")
            )
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_isNotCsrField_invalidForm_thenErrors(ValidationType validationType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getInvalidForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")
            )
        );
  }

  @Test
  void validatePartial_isCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validatePartial_isNotCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validateFull_isCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        )
        .doesNotContain(
            entry("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")
            )
        );
  }

  @Test
  void validateFull_isNotCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")
            )
        );
  }

  @Test
  void validateFull_emptyForm_hasNoAssociatedCondensateSelection_thenError() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getEmptyForm();
    form.setHasAssociatedCondensateRecoverableResources(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("hasAssociatedCondensateRecoverableResources",
                Set.of("hasAssociatedCondensateRecoverableResources.required"))
        );
  }

  @Test
  void validatePartial_emptyForm_HasAssociatedCondensateSelection_thenNoError() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getEmptyForm();
    form.setHasAssociatedCondensateRecoverableResources(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .doesNotContain(
            entry("hasAssociatedCondensateRecoverableResources",
                Set.of("hasAssociatedCondensateRecoverableResources.required"))
        );
  }

  private MixedCaseOutputsForm getValidForm() {
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    var oilRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );
    var associatedGasResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
    );
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    var gasRecoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    );
    var oilEquivalentResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );
    var associatedCondensateResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );
    var naturalGasLiquidsResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    );
    var salesGasResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    );

    var oilRecoverableReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
    );
    var associatedGasReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
    );
    var gasRecoverableReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
    );
    var associatedCondensateReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );
    var oilEquivalentReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );
    var naturalGasLiquidsReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
    );
    var salesGasReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    var form = new MixedCaseOutputsForm();

    form.setOilInPlace(oilInPlace);
    form.setGasInPlace(gasInPlace);

    form.setOilRecoverableResources(oilRecoverableResources);
    form.setAssociatedGasRecoverableResources(associatedGasResources);
    form.setGasRecoverableResources(gasRecoverableResources);
    form.setOilEquivalentRecoverableResources(oilEquivalentResources);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableResources(associatedCondensateResources);
    form.setNaturalGasLiquidsResources(naturalGasLiquidsResources);
    form.setSalesGasResources(salesGasResources);

    form.setOilRecoverableReserves(oilRecoverableReserves);
    form.setAssociatedGasRecoverableReserves(associatedGasReserves);
    form.setGasRecoverableReserves(gasRecoverableReserves);
    form.setAssociatedCondensateRecoverableReserves(associatedCondensateReserves);
    form.setOilEquivalentRecoverableReserves(oilEquivalentReserves);
    form.setNaturalGasLiquidsReserves(naturalGasLiquidsReserves);
    form.setSalesGasReserves(salesGasReserves);

    return form;
  }

  private MixedCaseOutputsForm getInvalidForm(SubmissionDetail submissionDetail) {
    var invalidDecimalInput = new DecimalInput("a");

    var probabilityDistributionFormHydrocarbonInPlace = new ProbabilityDistributionForm();
    probabilityDistributionFormHydrocarbonInPlace.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    probabilityDistributionFormHydrocarbonInPlace.getCustom().setP50(invalidDecimalInput);

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var custom = probabilityDistributionForm.getCustom();
    custom.setOneC(invalidDecimalInput);
    custom.setTwoC(invalidDecimalInput);
    custom.setThreeC(invalidDecimalInput);
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      custom.setOneP(invalidDecimalInput);
      custom.setTwoP(invalidDecimalInput);
      custom.setThreeP(invalidDecimalInput);
    }

    var form = new MixedCaseOutputsForm();

    form.setOilInPlace(probabilityDistributionFormHydrocarbonInPlace);
    form.setGasInPlace(probabilityDistributionFormHydrocarbonInPlace);

    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setNaturalGasLiquidsResources(probabilityDistributionForm);
    form.setSalesGasResources(probabilityDistributionForm);

    form.setOilRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedGasRecoverableReserves(probabilityDistributionForm);
    form.setGasRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionForm);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionForm);
    form.setNaturalGasLiquidsReserves(probabilityDistributionForm);
    form.setSalesGasReserves(probabilityDistributionForm);

    return form;
  }

  private MixedCaseOutputsForm getEmptyForm() {
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);

    var form = new MixedCaseOutputsForm();

    form.setOilInPlace(probabilityDistributionForm);
    form.setGasInPlace(probabilityDistributionForm);

    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setGasRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setNaturalGasLiquidsResources(probabilityDistributionForm);
    form.setSalesGasResources(probabilityDistributionForm);

    form.setOilRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedGasRecoverableReserves(probabilityDistributionForm);
    form.setGasRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionForm);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionForm);
    form.setNaturalGasLiquidsReserves(probabilityDistributionForm);
    form.setSalesGasReserves(probabilityDistributionForm);

    return form;
  }
}