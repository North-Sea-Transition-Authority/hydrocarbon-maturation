package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilvolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldOilCaseVolumetricTotalsFormValidatorTest {

  private FieldOilCaseVolumetricTotalsFormValidator fieldOilCaseVolumetricTotalsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    fieldOilCaseVolumetricTotalsFormValidator = new FieldOilCaseVolumetricTotalsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getFieldValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_isCsrField_invalidForm_thenErrors(ValidationType validationType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getInvalidForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
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
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
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
    assertFalse(fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @Test
  void validatePartial_isCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validatePartial_isNotCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validateFull_isCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
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
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
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
    assertFalse(fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedGasRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasReserves." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  private OilCaseOutputsForm getEmptyForm() {
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);

    var form = new OilCaseOutputsForm();

    form.setOilInPlace(probabilityDistributionForm);

    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setNaturalGasLiquidsResources(probabilityDistributionForm);
    form.setSalesGasResources(probabilityDistributionForm);

    form.setOilRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedGasRecoverableReserves(probabilityDistributionForm);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionForm);
    form.setNaturalGasLiquidsReserves(probabilityDistributionForm);
    form.setSalesGasReserves(probabilityDistributionForm);

    return form;
  }

  private OilCaseOutputsForm getFieldValidForm() {
    var oilInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    var recoverableResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );
    var oilEquivalentResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    );
    var associatedCondensateResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    );
    var associatedGasResources = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
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
    var associatedCondensateReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
    );
    var associatedGasReserves = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
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

    var form = new OilCaseOutputsForm();

    form.setOilInPlace(oilInPlace);

    form.setOilRecoverableResources(recoverableResources);
    form.setOilEquivalentRecoverableResources(oilEquivalentResources);
    form.setAssociatedCondensateRecoverableResources(associatedCondensateResources);
    form.setAssociatedGasRecoverableResources(associatedGasResources);
    form.setNaturalGasLiquidsResources(naturalGasLiquidsResources);
    form.setSalesGasResources(salesGasResources);

    form.setOilRecoverableReserves(oilRecoverableReserves);
    form.setAssociatedCondensateRecoverableReserves(associatedCondensateReserves);
    form.setAssociatedGasRecoverableReserves(associatedGasReserves);
    form.setOilEquivalentRecoverableReserves(oilEquivalentReserves);
    form.setNaturalGasLiquidsReserves(naturalGasLiquidsReserves);
    form.setSalesGasReserves(salesGasReserves);

    return form;
  }

  private OilCaseOutputsForm getInvalidForm(SubmissionDetail submissionDetail) {
    var invalidDecimalInput = new DecimalInput("a");

    var probabilityDistributionFormOilInPlace = new ProbabilityDistributionForm();
    probabilityDistributionFormOilInPlace.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    probabilityDistributionFormOilInPlace.getCustom().setP50(invalidDecimalInput);

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

    var form = new OilCaseOutputsForm();

    form.setOilInPlace(probabilityDistributionFormOilInPlace);

    form.setOilRecoverableResources(probabilityDistributionForm);
    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setAssociatedGasRecoverableResources(probabilityDistributionForm);
    form.setNaturalGasLiquidsResources(probabilityDistributionForm);
    form.setSalesGasResources(probabilityDistributionForm);

    form.setOilRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionForm);
    form.setAssociatedGasRecoverableReserves(probabilityDistributionForm);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionForm);
    form.setNaturalGasLiquidsReserves(probabilityDistributionForm);
    form.setSalesGasReserves(probabilityDistributionForm);

    return form;
  }
}
