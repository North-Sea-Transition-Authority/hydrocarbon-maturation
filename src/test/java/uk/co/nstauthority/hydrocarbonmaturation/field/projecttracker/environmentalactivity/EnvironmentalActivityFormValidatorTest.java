package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class EnvironmentalActivityFormValidatorTest {
  private EnvironmentalActivityFormValidator environmentalActivityFormValidator;

  private EnvironmentalActivityForm form;
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    environmentalActivityFormValidator = new EnvironmentalActivityFormValidator();

    form = new EnvironmentalActivityForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void isValid_emptyForm_thenTrue() {
    form = new EnvironmentalActivityForm();
    assertThat(environmentalActivityFormValidator.isValid(form, bindingResult)).isTrue();
  }

  @Test
  void isValid_allDatesProvided_andComplete_thenTrue() {
    form = getCompleteForm();
    assertThat(environmentalActivityFormValidator.isValid(form, bindingResult)).isTrue();
  }

  @Test
  void isValid_allDatesProvided_andAllAreToday_thenTrue() {
    form = getAllDatesTodayForm();
    assertThat(environmentalActivityFormValidator.isValid(form, bindingResult)).isTrue();
  }

  @Test
  void isValid_allDatesProvided_andInvalid_thenFalse() {
    form = getInvalidForm();
    assertThat(environmentalActivityFormValidator.isValid(form, bindingResult)).isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("submissionDateDay" , "submissionDateDay.invalid", "Submission date must be a real date"),
            Tuple.tuple("submissionDateMonth" , "submissionDateMonth.invalid", ""),
            Tuple.tuple("submissionDateYear" , "submissionDateYear.invalid", ""),

            Tuple.tuple("consultationStartDateDay" , "consultationStartDateDay.invalid", "Consultation start date must be a real date"),
            Tuple.tuple("consultationStartDateMonth" , "consultationStartDateMonth.invalid", ""),
            Tuple.tuple("consultationStartDateYear" , "consultationStartDateYear.invalid", ""),

            Tuple.tuple("consultationClosingDateDay" , "consultationClosingDateDay.invalid", "Consultation closing date must be a real date"),
            Tuple.tuple("consultationClosingDateMonth" , "consultationClosingDateMonth.invalid", ""),
            Tuple.tuple("consultationClosingDateYear" , "consultationClosingDateYear.invalid", "")
        );
  }

  @Test
  void isValid_allDatesProvided_anInFuture_thenFalse() {
    form = getAllDatesInFutureForm();
    assertThat(environmentalActivityFormValidator.isValid(form, bindingResult)).isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("submissionDateDay" , "submissionDateDay.notBeforeTargetDate", "Submission date must be today or in the past"),
            Tuple.tuple("submissionDateMonth" , "submissionDateMonth.notBeforeTargetDate", ""),
            Tuple.tuple("submissionDateYear" , "submissionDateYear.notBeforeTargetDate", ""),

            Tuple.tuple("consultationStartDateDay" , "consultationStartDateDay.notBeforeTargetDate", "Consultation start date must be today or in the past"),
            Tuple.tuple("consultationStartDateMonth" , "consultationStartDateMonth.notBeforeTargetDate", ""),
            Tuple.tuple("consultationStartDateYear" , "consultationStartDateYear.notBeforeTargetDate", ""),

            Tuple.tuple("consultationClosingDateDay" , "consultationClosingDateDay.notBeforeTargetDate", "Consultation closing date must be today or in the past"),
            Tuple.tuple("consultationClosingDateMonth" , "consultationClosingDateMonth.notBeforeTargetDate", ""),
            Tuple.tuple("consultationClosingDateYear" , "consultationClosingDateYear.notBeforeTargetDate", "")
        );
  }

  @Test
  void isValid_requiredDatesProvidedAndNotOthers_andValid_thenTrue() {
    form = getCompleteForm();
    form.setSubmissionDateYear(null);
    form.setSubmissionDateMonth(null);
    form.setSubmissionDateDay(null);

    assertThat(environmentalActivityFormValidator.isValid(form, bindingResult)).isTrue();
  }

  private EnvironmentalActivityForm getCompleteForm() {
    form = new EnvironmentalActivityForm();

    form.setSubmissionDateYear("2022");
    form.setSubmissionDateMonth("1");
    form.setSubmissionDateDay("25");

    form.setConsultationStartDateYear("2023");
    form.setConsultationStartDateMonth("2");
    form.setConsultationStartDateDay("26");

    form.setConsultationClosingDateYear("2024");
    form.setConsultationClosingDateMonth("3");
    form.setConsultationClosingDateDay("27");

    return form;
  }

  private EnvironmentalActivityForm getAllDatesTodayForm() {
    final var TODAY = LocalDate.now();
    
    form = new EnvironmentalActivityForm();

    form.setSubmissionDateYear(String.valueOf(TODAY.getYear()));
    form.setSubmissionDateMonth(String.valueOf(TODAY.getMonth().getValue()));
    form.setSubmissionDateDay(String.valueOf(TODAY.getDayOfMonth()));

    form.setConsultationStartDateYear(String.valueOf(TODAY.getYear()));
    form.setConsultationStartDateMonth(String.valueOf(TODAY.getMonth().getValue()));
    form.setConsultationStartDateDay(String.valueOf(TODAY.getDayOfMonth()));

    form.setConsultationClosingDateYear(String.valueOf(TODAY.getYear()));
    form.setConsultationClosingDateMonth(String.valueOf(TODAY.getMonth().getValue()));
    form.setConsultationClosingDateDay(String.valueOf(TODAY.getDayOfMonth()));

    return form;
  }

  private EnvironmentalActivityForm getAllDatesInFutureForm() {
    final var TOMORROW = LocalDate.now().plusDays(1);

    form = new EnvironmentalActivityForm();

    form.setSubmissionDateYear(String.valueOf(TOMORROW.getYear()));
    form.setSubmissionDateMonth(String.valueOf(TOMORROW.getMonth().getValue()));
    form.setSubmissionDateDay(String.valueOf(TOMORROW.getDayOfMonth()));

    form.setConsultationStartDateYear(String.valueOf(TOMORROW.getYear()));
    form.setConsultationStartDateMonth(String.valueOf(TOMORROW.getMonth().getValue()));
    form.setConsultationStartDateDay(String.valueOf(TOMORROW.getDayOfMonth()));

    form.setConsultationClosingDateYear(String.valueOf(TOMORROW.getYear()));
    form.setConsultationClosingDateMonth(String.valueOf(TOMORROW.getMonth().getValue()));
    form.setConsultationClosingDateDay(String.valueOf(TOMORROW.getDayOfMonth()));

    return form;
  }

  private EnvironmentalActivityForm getInvalidForm() {
    form = new EnvironmentalActivityForm();

    form.setSubmissionDateYear("not a year");
    form.setSubmissionDateMonth("not a month");
    form.setSubmissionDateDay("not a day");

    form.setConsultationStartDateYear("not a year");
    form.setConsultationStartDateMonth("not a month");
    form.setConsultationStartDateDay("not a day");

    form.setConsultationClosingDateYear("not a year");
    form.setConsultationClosingDateMonth("not a month");
    form.setConsultationClosingDateDay("not a day");

    return form;
  }
}