package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

class ProjectCapexFormValidatorTest {
  private ProjectCapexFormValidator projectCapexFormValidator;

  private ProjectCapexForm form;

  @BeforeEach
  void setUp() {
    projectCapexFormValidator = new ProjectCapexFormValidator();
    form = getValidForm();
  }

  @Test
  void isValid_emptyForm_thenFalse() {
    form = new ProjectCapexForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            tuple("dateProvidedDay", "dateProvidedDay.required", "Enter a date the project CAPEX was provided"),
            tuple("dateProvidedMonth", "dateProvidedMonth.required", ""),
            tuple( "dateProvidedYear", "dateProvidedYear.required", ""),
            tuple("totalCapex.decimalInputValue", "totalCapex.required", "Provide the total project CAPEX"),
            tuple("sourceOfInformation", "sourceOfInformation.required", "Select the source of information")
          );
  }

  @Test
  void isValid_completeForm_thenTrue() {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isTrue();

    assertThat(bindingResult.getFieldErrors()).isEmpty();
  }

  @Test
  void isValid_providedDateInvalid_thenFalse() {
    form.setDateProvidedDay("a");
    form.setDateProvidedMonth("b");
    form.setDateProvidedYear("c");

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            tuple("dateProvidedDay", "dateProvidedDay.invalid", "Date the project CAPEX was provided must be a real date"),
            tuple("dateProvidedMonth", "dateProvidedMonth.invalid", ""),
            tuple( "dateProvidedYear", "dateProvidedYear.invalid", "")
        );
  }

  @Test
  void isValid_providedDateInFuture_thenFalse() {
    final var TOMORROW = LocalDate.now().plusDays(1);
    form.setDateProvidedDay(String.valueOf(TOMORROW.getDayOfMonth()));
    form.setDateProvidedMonth(String.valueOf(TOMORROW.getMonthValue()));
    form.setDateProvidedYear(String.valueOf(TOMORROW.getYear()));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            tuple("dateProvidedDay", "dateProvidedDay.notBeforeTargetDate", "Date the project CAPEX was provided must be today or in the past"),
            tuple("dateProvidedMonth", "dateProvidedMonth.notBeforeTargetDate", ""),
            tuple( "dateProvidedYear", "dateProvidedYear.notBeforeTargetDate", ""));
  }

  @Test
  void isValid_providedDateIsToday_thenTrue() {
    final var TODAY = LocalDate.now();
    form.setDateProvidedDay(String.valueOf(TODAY.getDayOfMonth()));
    form.setDateProvidedMonth(String.valueOf(TODAY.getMonthValue()));
    form.setDateProvidedYear(String.valueOf(TODAY.getYear()));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isTrue();
    assertThat(bindingResult.getFieldErrors()).isEmpty();
  }

  @Test
  void isValid_totalCapexNonNumeric_thenFalse() {
    form.setTotalCapex(new DecimalInput("not a number"));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            tuple("totalCapex.decimalInputValue", "totalCapex.decimalInputValue.notNumeric", "Total project CAPEX must be a number"));
  }

  @Test
  void isValid_totalCapexPrecisionGreaterThanTwo_thenFalse() {
    form.setTotalCapex(new DecimalInput("15.0123"));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            tuple("totalCapex.decimalInputValue", "totalCapex.decimalInputValue.incorrectDecimalPlaces", "Total project CAPEX must have no more than 2 decimal places"));
  }

  @Test
  void isValid_totalCapexLessThanZero_thenFalse() {
    form.setTotalCapex(new DecimalInput("-12.5"));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThat(projectCapexFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            tuple("totalCapex.decimalInputValue", "totalCapex.decimalInputValue.lessThanMinValue", "Total project CAPEX must be 0 or more"));
  }

  private ProjectCapexForm getValidForm() {
    var form = new ProjectCapexForm();

    form.setDateProvidedDay("3");
    form.setDateProvidedMonth("4");
    form.setDateProvidedYear("2023");
    form.setTotalCapex(new DecimalInput("12.5"));
    form.setSourceOfInformation(ProjectCapexSource.APPROVED_FDP);

    return form;
  }
}