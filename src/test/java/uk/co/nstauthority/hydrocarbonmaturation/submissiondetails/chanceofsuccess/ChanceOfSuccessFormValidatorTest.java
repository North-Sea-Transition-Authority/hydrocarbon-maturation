package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class ChanceOfSuccessFormValidatorTest {

  private final ChanceOfSuccessFormValidator chanceOfSuccessFormValidator = new ChanceOfSuccessFormValidator();

  @Test
  void supports_whenGeneralDetailsFormClass_thenTrue() {
    var supportedClass = ChanceOfSuccessForm.class;
    assertTrue(chanceOfSuccessFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotGeneralDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(chanceOfSuccessFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResultFullValidation = validateChanceOfSuccessForm(form, ValidationType.FULL);
    var bindingResultPartialValidation = validateChanceOfSuccessForm(form, ValidationType.PARTIAL);

    assertFalse(bindingResultFullValidation.hasErrors());
    assertFalse(bindingResultPartialValidation.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenNoValidationErrors_onPartialValidation() {
    var form = new ChanceOfSuccessForm();
    form.setPlayChanceOfSuccess(new ChanceOfSuccessDetails());
    form.setProspectSpecificChanceOfSuccess(new ProspectSpecificChanceOfSuccess());
    var bindingResult = validateChanceOfSuccessForm(form, ValidationType.PARTIAL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors_onFullValidation() {
    var form = new ChanceOfSuccessForm();
    form.setPlayChanceOfSuccess(new ChanceOfSuccessDetails());
    form.setProspectSpecificChanceOfSuccess(new ProspectSpecificChanceOfSuccess());

    var bindingResult = validateChanceOfSuccessForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage", Set.of("playChanceOfSuccess.sourcePercentage.required")),
            entry("playChanceOfSuccess.reservoirPercentage", Set.of("playChanceOfSuccess.reservoirPercentage.required")),
            entry("playChanceOfSuccess.topSealPercentage", Set.of("playChanceOfSuccess.topSealPercentage.required")),
            entry("prospectSpecificChanceOfSuccess.sourcePercentage", Set.of("prospectSpecificChanceOfSuccess.sourcePercentage.required")),
            entry("prospectSpecificChanceOfSuccess.reservoirPercentage", Set.of("prospectSpecificChanceOfSuccess.reservoirPercentage.required")),
            entry("prospectSpecificChanceOfSuccess.topSealPercentage", Set.of("prospectSpecificChanceOfSuccess.topSealPercentage.required")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage", Set.of("prospectSpecificChanceOfSuccess.trapPercentage.required")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage", Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.required")),
            entry("geophysicallyModified", Set.of("geophysicallyModified.required")),
            entry("relatedToSpecificWellLocation", Set.of("relatedToSpecificWellLocation.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void validate_whenInValidForm_thenValidationErrors_maxValueExceeded(ValidationType validationType) {
    var form = new ChanceOfSuccessForm();
    var playChanceOfSuccess = new ChanceOfSuccessDetails();
    var prospectSpecific = new ProspectSpecificChanceOfSuccess();

    playChanceOfSuccess.setSourcePercentage("101");
    playChanceOfSuccess.setReservoirPercentage("101");
    playChanceOfSuccess.setTopSealPercentage("101");

    prospectSpecific.setSourcePercentage("101");
    prospectSpecific.setReservoirPercentage("101");
    prospectSpecific.setTopSealPercentage("101");
    prospectSpecific.setTrapPercentage("101");
    prospectSpecific.setMigrationPercentage("101");

    form.setPlayChanceOfSuccess(playChanceOfSuccess);
    form.setProspectSpecificChanceOfSuccess(prospectSpecific);
    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(new DecimalInput("101"));
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(new DecimalInput("101"));

    var bindingResult = validateChanceOfSuccessForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage", Set.of("playChanceOfSuccess.sourcePercentage.moreThanMaxValue")),
            entry("playChanceOfSuccess.reservoirPercentage", Set.of("playChanceOfSuccess.reservoirPercentage.moreThanMaxValue")),
            entry("playChanceOfSuccess.topSealPercentage", Set.of("playChanceOfSuccess.topSealPercentage.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.sourcePercentage", Set.of("prospectSpecificChanceOfSuccess.sourcePercentage.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.reservoirPercentage", Set.of("prospectSpecificChanceOfSuccess.reservoirPercentage.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.topSealPercentage", Set.of("prospectSpecificChanceOfSuccess.topSealPercentage.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage", Set.of("prospectSpecificChanceOfSuccess.trapPercentage.moreThanMaxValue")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage", Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.moreThanMaxValue")),
            entry("geophysicallyModifiedPercentage.decimalInputValue", Set.of("geophysicallyModifiedPercentage.decimalInputValue.moreThanMaxValue")),
            entry("wellModifiedPercentage.decimalInputValue", Set.of("wellModifiedPercentage.decimalInputValue.moreThanMaxValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void validate_whenInValidForm_thenValidationErrors_minValueExceeded(ValidationType validationType) {
    var form = new ChanceOfSuccessForm();
    var playChanceOfSuccess = new ChanceOfSuccessDetails();
    var prospectSpecific = new ProspectSpecificChanceOfSuccess();

    playChanceOfSuccess.setSourcePercentage("0");
    playChanceOfSuccess.setReservoirPercentage("0");
    playChanceOfSuccess.setTopSealPercentage("0");

    prospectSpecific.setSourcePercentage("0");
    prospectSpecific.setReservoirPercentage("0");
    prospectSpecific.setTopSealPercentage("0");
    prospectSpecific.setTrapPercentage("0");
    prospectSpecific.setMigrationPercentage("0");

    form.setPlayChanceOfSuccess(playChanceOfSuccess);
    form.setProspectSpecificChanceOfSuccess(prospectSpecific);
    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(new DecimalInput("0"));
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(new DecimalInput("0"));

    var bindingResult = validateChanceOfSuccessForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage", Set.of("playChanceOfSuccess.sourcePercentage.lessThanMinValue")),
            entry("playChanceOfSuccess.reservoirPercentage", Set.of("playChanceOfSuccess.reservoirPercentage.lessThanMinValue")),
            entry("playChanceOfSuccess.topSealPercentage", Set.of("playChanceOfSuccess.topSealPercentage.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.sourcePercentage", Set.of("prospectSpecificChanceOfSuccess.sourcePercentage.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.reservoirPercentage", Set.of("prospectSpecificChanceOfSuccess.reservoirPercentage.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.topSealPercentage", Set.of("prospectSpecificChanceOfSuccess.topSealPercentage.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage", Set.of("prospectSpecificChanceOfSuccess.trapPercentage.lessThanMinValue")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage", Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.lessThanMinValue")),
            entry("geophysicallyModifiedPercentage.decimalInputValue", Set.of("geophysicallyModifiedPercentage.decimalInputValue.lessThanMinValue")),
            entry("wellModifiedPercentage.decimalInputValue", Set.of("wellModifiedPercentage.decimalInputValue.lessThanMinValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void validate_whenInValidForm_thenValidationErrors_wrongDecimalPlaces(ValidationType validationType) {
    var form = new ChanceOfSuccessForm();
    var playChanceOfSuccess = new ChanceOfSuccessDetails();
    var prospectSpecific = new ProspectSpecificChanceOfSuccess();

    playChanceOfSuccess.setSourcePercentage("1.1");
    playChanceOfSuccess.setReservoirPercentage("1.1");
    playChanceOfSuccess.setTopSealPercentage("1.1");

    prospectSpecific.setSourcePercentage("1.1");
    prospectSpecific.setReservoirPercentage("1.1");
    prospectSpecific.setTopSealPercentage("1.1");
    prospectSpecific.setTrapPercentage("1.1");
    prospectSpecific.setMigrationPercentage("1.1");

    form.setPlayChanceOfSuccess(playChanceOfSuccess);
    form.setProspectSpecificChanceOfSuccess(prospectSpecific);
    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(new DecimalInput("1.1"));
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(new DecimalInput("1.1"));

    var bindingResult = validateChanceOfSuccessForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("playChanceOfSuccess.sourcePercentage", Set.of("playChanceOfSuccess.sourcePercentage.incorrectDecimalPlaces")),
            entry("playChanceOfSuccess.reservoirPercentage", Set.of("playChanceOfSuccess.reservoirPercentage.incorrectDecimalPlaces")),
            entry("playChanceOfSuccess.topSealPercentage", Set.of("playChanceOfSuccess.topSealPercentage.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.sourcePercentage", Set.of("prospectSpecificChanceOfSuccess.sourcePercentage.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.reservoirPercentage", Set.of("prospectSpecificChanceOfSuccess.reservoirPercentage.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.topSealPercentage", Set.of("prospectSpecificChanceOfSuccess.topSealPercentage.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.trapPercentage", Set.of("prospectSpecificChanceOfSuccess.trapPercentage.incorrectDecimalPlaces")),
            entry("prospectSpecificChanceOfSuccess.migrationPercentage", Set.of("prospectSpecificChanceOfSuccess.migrationPercentage.incorrectDecimalPlaces")),
            entry("geophysicallyModifiedPercentage.decimalInputValue", Set.of("geophysicallyModifiedPercentage.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellModifiedPercentage.decimalInputValue", Set.of("wellModifiedPercentage.decimalInputValue.incorrectDecimalPlaces"))
        );
  }

  private BindingResult validateChanceOfSuccessForm(ChanceOfSuccessForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    chanceOfSuccessFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  private ChanceOfSuccessForm getValidForm() {
    var form = new ChanceOfSuccessForm();
    var playChanceOfSuccess = new ChanceOfSuccessDetails();
    var prospectSpecific = new ProspectSpecificChanceOfSuccess();

    playChanceOfSuccess.setSourcePercentage("1");
    playChanceOfSuccess.setReservoirPercentage("1");
    playChanceOfSuccess.setTopSealPercentage("1");

    prospectSpecific.setSourcePercentage("1");
    prospectSpecific.setReservoirPercentage("1");
    prospectSpecific.setTopSealPercentage("1");
    prospectSpecific.setTrapPercentage("1");
    prospectSpecific.setMigrationPercentage("1");

    form.setPlayChanceOfSuccess(playChanceOfSuccess);
    form.setProspectSpecificChanceOfSuccess(prospectSpecific);
    form.setGeophysicallyModified(false);
    form.setRelatedToSpecificWellLocation(false);

    return form;
  }
}