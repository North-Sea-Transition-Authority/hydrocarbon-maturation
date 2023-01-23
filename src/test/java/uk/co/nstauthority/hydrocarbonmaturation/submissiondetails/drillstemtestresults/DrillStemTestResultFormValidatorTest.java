package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DrillStemTestResultFormValidatorTest {

  @InjectMocks
  private DrillStemTestResultFormValidator drillStemTestResultFormValidator;

  static private SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void supports_whenDrillStemTestResultFormClass_thenTrue() {
    var supportedClass = DrillStemTestResultForm.class;
    assertTrue(drillStemTestResultFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotDrillStemTestResultFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(drillStemTestResultFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestResultFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new DrillStemTestResultForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestResultFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("formationName", Set.of("formationName.required")),
            entry("topDepth.decimalInputValue", Set.of("topDepth.decimalInputValue.required")),
            entry("bottomDepth.decimalInputValue", Set.of("bottomDepth.decimalInputValue.required")),
            entry("bhfp.decimalInputValue", Set.of("bhfp.decimalInputValue.required")),
            entry("whfp.decimalInputValue", Set.of("whfp.decimalInputValue.required")),
            entry("flowDuration.decimalInputValue", Set.of("flowDuration.decimalInputValue.required")),
            entry("gasFlowrate.decimalInputValue", Set.of("gasFlowrate.decimalInputValue.required")),
            entry("oilFlowrate.decimalInputValue", Set.of("oilFlowrate.decimalInputValue.required")),
            entry("waterFlowrate.decimalInputValue", Set.of("waterFlowrate.decimalInputValue.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenWronglyFilledForm_thenValidationErrors(ValidationType validationType) {
    var form = getFormWithWrongValues();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestResultFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("topDepth.decimalInputValue", Set.of("topDepth.decimalInputValue.lessThanMinValue")),
            entry("bottomDepth.decimalInputValue", Set.of("bottomDepth.decimalInputValue.lessThanMinValue")),
            entry("bhfp.decimalInputValue", Set.of("bhfp.decimalInputValue.lessThanMinValue")),
            entry("whfp.decimalInputValue", Set.of("whfp.decimalInputValue.lessThanMinValue")),
            entry("flowDuration.decimalInputValue", Set.of("flowDuration.decimalInputValue.lessThanMinValue")),
            entry("gasFlowrate.decimalInputValue", Set.of("gasFlowrate.decimalInputValue.lessThanMinValue")),
            entry("oilFlowrate.decimalInputValue", Set.of("oilFlowrate.decimalInputValue.lessThanMinValue")),
            entry("waterFlowrate.decimalInputValue", Set.of("waterFlowrate.decimalInputValue.lessThanMinValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenWronglyDecimalPoints_thenValidationErrors(ValidationType validationType) {
    var form = getFormWithWrongDecimalPointValues();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestResultFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("topDepth.decimalInputValue", Set.of("topDepth.decimalInputValue.incorrectDecimalPlaces")),
            entry("bottomDepth.decimalInputValue", Set.of("bottomDepth.decimalInputValue.incorrectDecimalPlaces")),
            entry("bhfp.decimalInputValue", Set.of("bhfp.decimalInputValue.incorrectDecimalPlaces")),
            entry("whfp.decimalInputValue", Set.of("whfp.decimalInputValue.incorrectDecimalPlaces")),
            entry("flowDuration.decimalInputValue", Set.of("flowDuration.decimalInputValue.incorrectDecimalPlaces")),
            entry("gasFlowrate.decimalInputValue", Set.of("gasFlowrate.decimalInputValue.incorrectDecimalPlaces")),
            entry("oilFlowrate.decimalInputValue", Set.of("oilFlowrate.decimalInputValue.incorrectDecimalPlaces")),
            entry("waterFlowrate.decimalInputValue", Set.of("waterFlowrate.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  private DrillStemTestResultForm getFormWithWrongDecimalPointValues() {
    var form = new DrillStemTestResultForm();
    form.setBhfp(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.5)));
    form.setBottomDepth(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.555)));
    form.setFlowDuration(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.555)));
    form.setGasFlowrate(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.55)));
    form.setOilFlowrate(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.55)));
    form.setWaterFlowrate(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.55)));
    form.setWhfp(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.5)));
    form.setTopDepth(DecimalInput.createDecimalInput(BigDecimal.valueOf(2.555)));
    form.setCommentsInterpretation("comment");
    form.setFormationName("name");
    return form;
  }

  private DrillStemTestResultForm getFormWithWrongValues() {
    var form = new DrillStemTestResultForm();
    form.setBhfp(DecimalInput.createDecimalInput(-1));
    form.setBottomDepth(DecimalInput.createDecimalInput(-1));
    form.setFlowDuration(DecimalInput.createDecimalInput(-1));
    form.setGasFlowrate(DecimalInput.createDecimalInput(-1));
    form.setOilFlowrate(DecimalInput.createDecimalInput(-1));
    form.setWaterFlowrate(DecimalInput.createDecimalInput(-1));
    form.setWhfp(DecimalInput.createDecimalInput(-1));
    form.setTopDepth(DecimalInput.createDecimalInput(-1));
    form.setCommentsInterpretation("comment");
    form.setFormationName("name");
    return form;
  }

  private DrillStemTestResultForm getValidForm() {
    return DrillStemTestResultPersistenceServiceTest.getFilledDrillStemTestResultForm();
  }
}