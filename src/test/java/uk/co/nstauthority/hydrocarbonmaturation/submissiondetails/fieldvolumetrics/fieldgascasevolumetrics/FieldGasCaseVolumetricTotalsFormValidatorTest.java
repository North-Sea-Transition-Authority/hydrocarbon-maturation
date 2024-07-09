package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldGasCaseVolumetricTotalsFormValidatorTest {

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private FieldGasCaseVolumetricTotalsFormValidator fieldGasCaseVolumetricTotalsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_COP", "CREATE_FDP", "CREATE_FDPA"})
  void validatePartial_whenValidForm_andIsCopFdpOrFdpa_withCorrectVolumetricsQuestion_thenNoValidationErrors(
      SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = getFieldValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail,
        ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_COP", "CREATE_FDP", "CREATE_FDPA"})
  void validatePartial_whenValidForm_andIsCopFdpOrFdpa_withoutCorrectVolumetricsQuestion_thenNoValidationErrors(
      SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = getFieldValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail,
        ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_COP", "CREATE_FDP", "CREATE_FDPA"})
  void validateFull_whenValidForm_andIsCopFdpOrFdpa_withCorrectVolumetricsQuestion_thenNoValidationErrors(
      SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(true);

    var form = getFieldValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail,
        ValidationType.FULL));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_COP", "CREATE_FDP", "CREATE_FDPA"})
  void validateFull_whenValidForm_andIsCopFdpOrFdpa_withoutCorrectVolumetricsQuestion_thenNoValidationErrors(
      SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(false);

    var form = getFieldValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail,
        ValidationType.FULL));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getFieldValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_isCsrField_invalidForm_thenErrors(ValidationType validationType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getInvalidForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
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
    assertFalse(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

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
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validatePartial_isNotCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validateFull_isCsrField_emptyForm_thenErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
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
    assertFalse(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

            entry("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("associatedCondensateRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilEquivalentRecoverableResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("naturalGasLiquidsResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("salesGasResources." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),

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
    assertFalse(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("hasAssociatedCondensateRecoverableResources",
                Set.of("hasAssociatedCondensateRecoverableResources.required"))
            );
  }

  @Test
  void validatePartial_emptyForm_HasAssociatedCondensateSelection_thenError() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getEmptyForm();
    form.setHasAssociatedCondensateRecoverableResources(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(fieldGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .doesNotContain(
            entry("hasAssociatedCondensateRecoverableResources",
                Set.of("hasAssociatedCondensateRecoverableResources.required"))
        );
  }

  private GasCaseOutputsForm getEmptyForm() {
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);

    var form = new GasCaseOutputsForm();

    form.setGasInPlace(probabilityDistributionForm);

    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setNaturalGasLiquidsResources(probabilityDistributionForm);
    form.setSalesGasResources(probabilityDistributionForm);

    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionForm);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionForm);
    form.setNaturalGasLiquidsReserves(probabilityDistributionForm);
    form.setSalesGasReserves(probabilityDistributionForm);

    return form;
  }

  private GasCaseOutputsForm getFieldValidForm() {
    var gasInPlace = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
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

    var form = new GasCaseOutputsForm();

    form.setGasInPlace(gasInPlace);

    form.setOilEquivalentRecoverableResources(oilEquivalentResources);
    form.setAssociatedCondensateRecoverableResources(associatedCondensateResources);
    form.setNaturalGasLiquidsResources(naturalGasLiquidsResources);
    form.setSalesGasResources(salesGasResources);

    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableReserves(associatedCondensateReserves);
    form.setOilEquivalentRecoverableReserves(oilEquivalentReserves);
    form.setNaturalGasLiquidsReserves(naturalGasLiquidsReserves);
    form.setSalesGasReserves(salesGasReserves);

    form.setFieldVolumetricInformationCorrect(Boolean.TRUE);

    return form;
  }

  private GasCaseOutputsForm getInvalidForm(SubmissionDetail submissionDetail) {
    var invalidDecimalInput = new DecimalInput("a");

    var probabilityDistributionFormGasInPlace = new ProbabilityDistributionForm();
    probabilityDistributionFormGasInPlace.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    probabilityDistributionFormGasInPlace.getCustom().setP50(invalidDecimalInput);

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

    var form = new GasCaseOutputsForm();

    form.setGasInPlace(probabilityDistributionFormGasInPlace);

    form.setOilEquivalentRecoverableResources(probabilityDistributionForm);
    form.setHasAssociatedCondensateRecoverableResources(true);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionForm);
    form.setNaturalGasLiquidsResources(probabilityDistributionForm);
    form.setSalesGasResources(probabilityDistributionForm);

    form.setAssociatedCondensateRecoverableReserves(probabilityDistributionForm);
    form.setOilEquivalentRecoverableReserves(probabilityDistributionForm);
    form.setNaturalGasLiquidsReserves(probabilityDistributionForm);
    form.setSalesGasReserves(probabilityDistributionForm);

    return form;
  }
}