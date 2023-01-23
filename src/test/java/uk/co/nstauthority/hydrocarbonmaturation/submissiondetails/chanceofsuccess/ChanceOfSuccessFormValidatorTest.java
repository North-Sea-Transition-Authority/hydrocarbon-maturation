package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class ChanceOfSuccessFormValidatorTest {

  private final ChanceOfSuccessFormValidator chanceOfSuccessFormValidator = new ChanceOfSuccessFormValidator();

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenChanceOfSuccessFormClass_thenTrue() {
    var supportedClass = ChanceOfSuccessForm.class;
    assertTrue(chanceOfSuccessFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotChanceOfSuccessFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(chanceOfSuccessFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = ChanceOfSuccessTestUtil.createChanceOfSuccessForm();

    var bindingResultFullValidation = validateChanceOfSuccessForm(form, ValidationType.FULL);
    var bindingResultPartialValidation = validateChanceOfSuccessForm(form, ValidationType.PARTIAL);

    assertFalse(bindingResultFullValidation.hasErrors());
    assertFalse(bindingResultPartialValidation.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenNoValidationErrors_onPartialValidation() {
    var form = ChanceOfSuccessTestUtil.createEmptyChanceOfSuccessForm();
    var bindingResult = validateChanceOfSuccessForm(form, ValidationType.PARTIAL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors_onFullValidation() {
    var form = ChanceOfSuccessTestUtil.createEmptyChanceOfSuccessForm();

    var bindingResult = validateChanceOfSuccessForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.sourcePercentage.decimalInputValue.required")),
            entry("playChanceOfSuccess.reservoirPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.reservoirPercentage.decimalInputValue.required")),
            entry("playChanceOfSuccess.topSealPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.topSealPercentage.decimalInputValue.required")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue.required")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue.required")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue.required")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue.required")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue.required")),
            entry("geophysicallyModified",
                Set.of("geophysicallyModified.required")),
            entry("relatedToSpecificWellLocation",
                Set.of("relatedToSpecificWellLocation.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void validate_whenInValidForm_thenValidationErrors_maxValueExceeded(ValidationType validationType) {
    var form = ChanceOfSuccessTestUtil.createEmptyChanceOfSuccessForm();

    var chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(new DecimalInput("101"));
    chanceOfSuccessDetailsForm.setReservoirPercentage(new DecimalInput("101"));
    chanceOfSuccessDetailsForm.setTopSealPercentage(new DecimalInput("101"));

    form.setPlayChanceOfSuccess(chanceOfSuccessDetailsForm);

    var prospectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();
    prospectSpecificChanceOfSuccessForm.setChanceOfSuccessDetailsForm(chanceOfSuccessDetailsForm);
    prospectSpecificChanceOfSuccessForm.setTrapPercentage(new DecimalInput("101"));
    prospectSpecificChanceOfSuccessForm.setMigrationPercentage(new DecimalInput("101"));

    form.setProspectSpecificChanceOfSuccess(prospectSpecificChanceOfSuccessForm);

    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(new DecimalInput("101"));
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(new DecimalInput("101"));

    var bindingResult = validateChanceOfSuccessForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.sourcePercentage.decimalInputValue.moreThanMaxValue")),
            entry("playChanceOfSuccess.reservoirPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.reservoirPercentage.decimalInputValue.moreThanMaxValue")),
            entry("playChanceOfSuccess.topSealPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.topSealPercentage.decimalInputValue.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue.moreThanMaxValue")),
            entry("geophysicallyModifiedPercentage.decimalInputValue",
                Set.of("geophysicallyModifiedPercentage.decimalInputValue.moreThanMaxValue")),
            entry("wellModifiedPercentage.decimalInputValue",
                Set.of("wellModifiedPercentage.decimalInputValue.moreThanMaxValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void validate_whenInValidForm_thenValidationErrors_minValueExceeded(ValidationType validationType) {
    var form = ChanceOfSuccessTestUtil.createEmptyChanceOfSuccessForm();

    var chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(new DecimalInput("0"));
    chanceOfSuccessDetailsForm.setReservoirPercentage(new DecimalInput("0"));
    chanceOfSuccessDetailsForm.setTopSealPercentage(new DecimalInput("0"));

    form.setPlayChanceOfSuccess(chanceOfSuccessDetailsForm);

    var prospectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();
    prospectSpecificChanceOfSuccessForm.setChanceOfSuccessDetailsForm(chanceOfSuccessDetailsForm);
    prospectSpecificChanceOfSuccessForm.setTrapPercentage(new DecimalInput("0"));
    prospectSpecificChanceOfSuccessForm.setMigrationPercentage(new DecimalInput("0"));

    form.setProspectSpecificChanceOfSuccess(prospectSpecificChanceOfSuccessForm);

    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(new DecimalInput("0"));
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(new DecimalInput("0"));

    var bindingResult = validateChanceOfSuccessForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.sourcePercentage.decimalInputValue.lessThanMinValue")),
            entry("playChanceOfSuccess.reservoirPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.reservoirPercentage.decimalInputValue.lessThanMinValue")),
            entry("playChanceOfSuccess.topSealPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.topSealPercentage.decimalInputValue.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue.lessThanMinValue")),
            entry("geophysicallyModifiedPercentage.decimalInputValue",
                Set.of("geophysicallyModifiedPercentage.decimalInputValue.lessThanMinValue")),
            entry("wellModifiedPercentage.decimalInputValue",
                Set.of("wellModifiedPercentage.decimalInputValue.lessThanMinValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void validate_whenInValidForm_thenValidationErrors_wrongDecimalPlaces(ValidationType validationType) {
    var form = ChanceOfSuccessTestUtil.createEmptyChanceOfSuccessForm();

    var chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(new DecimalInput("1.1"));
    chanceOfSuccessDetailsForm.setReservoirPercentage(new DecimalInput("1.1"));
    chanceOfSuccessDetailsForm.setTopSealPercentage(new DecimalInput("1.1"));

    form.setPlayChanceOfSuccess(chanceOfSuccessDetailsForm);

    var prospectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();
    prospectSpecificChanceOfSuccessForm.setChanceOfSuccessDetailsForm(chanceOfSuccessDetailsForm);
    prospectSpecificChanceOfSuccessForm.setTrapPercentage(new DecimalInput("1.1"));
    prospectSpecificChanceOfSuccessForm.setMigrationPercentage(new DecimalInput("1.1"));

    form.setProspectSpecificChanceOfSuccess(prospectSpecificChanceOfSuccessForm);

    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(new DecimalInput("1.1"));
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(new DecimalInput("1.1"));

    var bindingResult = validateChanceOfSuccessForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.sourcePercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("playChanceOfSuccess.reservoirPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.reservoirPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("playChanceOfSuccess.topSealPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.topSealPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("geophysicallyModifiedPercentage.decimalInputValue",
                Set.of("geophysicallyModifiedPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellModifiedPercentage.decimalInputValue",
                Set.of("wellModifiedPercentage.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void validate_whenInValidForm_thenValidationErrors_notNumeric(ValidationType validationType) {
    var form = ChanceOfSuccessTestUtil.createEmptyChanceOfSuccessForm();

    var chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(new DecimalInput("a"));
    chanceOfSuccessDetailsForm.setReservoirPercentage(new DecimalInput("a"));
    chanceOfSuccessDetailsForm.setTopSealPercentage(new DecimalInput("a"));

    form.setPlayChanceOfSuccess(chanceOfSuccessDetailsForm);

    var prospectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();
    prospectSpecificChanceOfSuccessForm.setChanceOfSuccessDetailsForm(chanceOfSuccessDetailsForm);
    prospectSpecificChanceOfSuccessForm.setTrapPercentage(new DecimalInput("a"));
    prospectSpecificChanceOfSuccessForm.setMigrationPercentage(new DecimalInput("a"));

    form.setProspectSpecificChanceOfSuccess(prospectSpecificChanceOfSuccessForm);

    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(new DecimalInput("a"));
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(new DecimalInput("a"));

    var bindingResult = validateChanceOfSuccessForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.sourcePercentage.decimalInputValue.notNumeric")),
            entry("playChanceOfSuccess.reservoirPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.reservoirPercentage.decimalInputValue.notNumeric")),
            entry("playChanceOfSuccess.topSealPercentage.decimalInputValue",
                Set.of("playChanceOfSuccess.topSealPercentage.decimalInputValue.notNumeric")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue.notNumeric")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue.notNumeric")),
            entry("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue.notNumeric")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue.notNumeric")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue",
                Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue.notNumeric")),
            entry("geophysicallyModifiedPercentage.decimalInputValue",
                Set.of("geophysicallyModifiedPercentage.decimalInputValue.notNumeric")),
            entry("wellModifiedPercentage.decimalInputValue",
                Set.of("wellModifiedPercentage.decimalInputValue.notNumeric"))
        );
  }

  private BindingResult validateChanceOfSuccessForm(ChanceOfSuccessForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    chanceOfSuccessFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }
}