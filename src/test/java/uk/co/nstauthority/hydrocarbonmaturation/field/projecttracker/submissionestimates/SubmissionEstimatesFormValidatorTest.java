package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@ExtendWith(MockitoExtension.class)
class SubmissionEstimatesFormValidatorTest {
  @Mock
  private SubmissionEstimatesService submissionEstimatesService;
  @InjectMocks
  private SubmissionEstimatesFormValidator submissionEstimatesFormValidator;

  private SubmissionEstimatesForm form;
  private BindingResult bindingResult;
  private Project project;

  @BeforeEach
  void setUp() {
    form = getValidForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Nested
  class GivenIsValid {
    @Test
    void whenValidForm_thenTrue() {
      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isTrue();

      assertThat(bindingResult.hasErrors()).isFalse();
    }
  }

  @Nested
  class GivenNotAllDatesProvided {
    @BeforeEach
    void setUp() {
      form = getValidForm();
    }

    @Test
    void whenNoneProvided_thenFalse() {
      form = new SubmissionEstimatesForm();

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();

      assertThat(bindingResult.getFieldErrors())
          .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
          .containsExactly(
              Tuple.tuple("mostLikelyDateDay", "mostLikelyDateDay.required", "Enter a most likely submission date"),
              Tuple.tuple("mostLikelyDateMonth", "mostLikelyDateMonth.required", ""),
              Tuple.tuple("mostLikelyDateYear", "mostLikelyDateYear.required", ""),

              Tuple.tuple("estimatedDateDay", "estimatedDateDay.required", "Enter a date these were the estimated submission dates"),
              Tuple.tuple("estimatedDateMonth", "estimatedDateMonth.required", ""),
              Tuple.tuple("estimatedDateYear", "estimatedDateYear.required", "")
          );
    }

    @Test
    void whenNoEarliestDate_thenTrue() {
      form.setEarliestDateDay(null);
      form.setEarliestDateMonth(null);
      form.setEarliestDateYear(null);

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isTrue();
    }

    @Test
    void whenNoMostLikelyDate_thenFalse() {
      form.setMostLikelyDateDay(null);
      form.setMostLikelyDateMonth(null);
      form.setMostLikelyDateYear(null);

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();

      assertThat(bindingResult.getFieldErrors())
          .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
          .containsExactly(
              Tuple.tuple("mostLikelyDateDay", "mostLikelyDateDay.required", "Enter a most likely submission date"),
              Tuple.tuple("mostLikelyDateMonth", "mostLikelyDateMonth.required", ""),
              Tuple.tuple("mostLikelyDateYear", "mostLikelyDateYear.required", "")
          );
    }

    @Test
    void whenNoLatestDate_thenTrue() {
      form.setLatestDateDay(null);
      form.setLatestDateMonth(null);
      form.setLatestDateYear(null);

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isTrue();
    }

    @Test
    void whenNoEstimatedDate_thenFalse() {
      form.setEstimatedDateDay(null);
      form.setEstimatedDateMonth(null);
      form.setEstimatedDateYear(null);

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();

      assertThat(bindingResult.getFieldErrors())
          .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
          .containsExactly(
              Tuple.tuple("estimatedDateDay", "estimatedDateDay.required", "Enter a date these were the estimated submission dates"),
              Tuple.tuple("estimatedDateMonth", "estimatedDateMonth.required", ""),
              Tuple.tuple("estimatedDateYear", "estimatedDateYear.required", "")
          );
    }
  }

  @Nested
  class GivenDatesMustBeOrdered {

    @Test
    void whenEarliestIsAfterLatest_thenFalse() {
      form.setEarliestDateDay("3");
      form.setEarliestDateMonth("11");
      form.setEarliestDateYear("2024");

      form.setMostLikelyDateDay("3");
      form.setMostLikelyDateMonth("11");
      form.setMostLikelyDateYear("2024");

      form.setLatestDateDay("3");
      form.setLatestDateMonth("11");
      form.setLatestDateYear("2010");

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();

      assertThat(bindingResult.getFieldErrors())
          .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
          .containsExactly(
              Tuple.tuple("earliestDateDay", "earliestDateDay.notBeforeTargetDate", "Earliest date must be on or before the latest date"),
              Tuple.tuple("earliestDateMonth", "earliestDateMonth.notBeforeTargetDate", ""),
              Tuple.tuple("earliestDateYear", "earliestDateYear.notBeforeTargetDate", ""),

              Tuple.tuple("mostLikelyDateDay", "mostLikelyDateDay.notBeforeTargetDate", "Most likely date must be on or before the latest date"),
              Tuple.tuple("mostLikelyDateMonth", "mostLikelyDateMonth.notBeforeTargetDate", ""),
              Tuple.tuple("mostLikelyDateYear", "mostLikelyDateYear.notBeforeTargetDate", "")
          );
    }

    @Test
    void whenEarliestEqualsLatest_thenTrue() {
      form.setEarliestDateDay("3");
      form.setEarliestDateMonth("11");
      form.setEarliestDateYear("2024");

      // must also be the same to avoid another validation error in test
      form.setMostLikelyDateDay("3");
      form.setMostLikelyDateMonth("11");
      form.setMostLikelyDateYear("2024");

      form.setLatestDateDay("3");
      form.setLatestDateMonth("11");
      form.setLatestDateYear("2024");

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isTrue();
    }

    @Test
    void whenEarliestIsAfterMostLikely_thenFalse() {
      form.setEarliestDateDay("3");
      form.setEarliestDateMonth("11");
      form.setEarliestDateYear("2024");

      form.setMostLikelyDateDay("3");
      form.setMostLikelyDateMonth("11");
      form.setMostLikelyDateYear("2023");

      form.setLatestDateDay("3");
      form.setLatestDateMonth("11");
      form.setLatestDateYear("2025");

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();

      assertThat(bindingResult.getFieldErrors())
          .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
          .containsExactly(
              Tuple.tuple("earliestDateDay", "earliestDateDay.notBeforeTargetDate", "Earliest date must be on or before the most likely date"),
              Tuple.tuple("earliestDateMonth", "earliestDateMonth.notBeforeTargetDate", ""),
              Tuple.tuple("earliestDateYear", "earliestDateYear.notBeforeTargetDate", "")
          );
    }

    @Test
    void whenEarliestEqualsMostLikely_thenTrue() {
      form.setEarliestDateDay("3");
      form.setEarliestDateMonth("11");
      form.setEarliestDateYear("2024");

      form.setMostLikelyDateDay("3");
      form.setMostLikelyDateMonth("11");
      form.setMostLikelyDateYear("2024");

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isTrue();
    }

    @Test
    void whenMostLikelyIsAfterLatest_thenFalse() {
      form.setMostLikelyDateDay("3");
      form.setMostLikelyDateMonth("11");
      form.setMostLikelyDateYear("2024");

      form.setLatestDateDay("3");
      form.setLatestDateMonth("11");
      form.setLatestDateYear("2023");

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();

      assertThat(bindingResult.getFieldErrors())
          .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
          .containsExactly(
              Tuple.tuple("mostLikelyDateDay", "mostLikelyDateDay.notBeforeTargetDate", "Most likely date must be on or before the latest date"),
              Tuple.tuple("mostLikelyDateMonth", "mostLikelyDateMonth.notBeforeTargetDate", ""),
              Tuple.tuple("mostLikelyDateYear", "mostLikelyDateYear.notBeforeTargetDate", "")
          );
    }

    @Test
    void whenMostLikelyIsAfterLatest_thenTrue() {
      form.setMostLikelyDateDay("3");
      form.setMostLikelyDateMonth("11");
      form.setMostLikelyDateYear("2025");

      form.setLatestDateDay("3");
      form.setLatestDateMonth("11");
      form.setLatestDateYear("2024");

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();
    }

    @Test
    void whenEstimatedIsInTheFuture_thenFalse() {
      form.setEstimatedDateDay("3");
      form.setEstimatedDateMonth("11");
      form.setEstimatedDateYear(String.valueOf(LocalDate.now().getYear() + 1));

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isFalse();
    }

    @Test
    void whenEstimatedIsToday_thenTrue() {
      var today = LocalDate.now();

      form.setEstimatedDateDay(String.valueOf(today.getDayOfMonth()));
      form.setEstimatedDateMonth(String.valueOf(today.getMonthValue()));
      form.setEstimatedDateYear(String.valueOf(today.getYear()));

      assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
          .isTrue();
    }
  }

  @Test
  void givenDatesAreNotRealDates_thenErrors() {
    form = getInvalidForm();

    assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("earliestDateDay", "earliestDateDay.invalid", "Earliest submission date must be a real date"),
            Tuple.tuple("earliestDateMonth", "earliestDateMonth.invalid", ""),
            Tuple.tuple("earliestDateYear", "earliestDateYear.invalid", ""),

            Tuple.tuple("mostLikelyDateDay", "mostLikelyDateDay.invalid", "Most likely submission date must be a real date"),
            Tuple.tuple("mostLikelyDateMonth", "mostLikelyDateMonth.invalid", ""),
            Tuple.tuple("mostLikelyDateYear", "mostLikelyDateYear.invalid", ""),

            Tuple.tuple("latestDateDay", "latestDateDay.invalid", "Latest submission date must be a real date"),
            Tuple.tuple("latestDateMonth", "latestDateMonth.invalid", ""),
            Tuple.tuple("latestDateYear", "latestDateYear.invalid", ""),

            Tuple.tuple("estimatedDateDay", "estimatedDateDay.invalid", "Date these were the estimated submission dates must be a real date"),
            Tuple.tuple("estimatedDateMonth", "estimatedDateMonth.invalid", ""),
            Tuple.tuple("estimatedDateYear", "estimatedDateYear.invalid", "")
        );
  }

  @Test
  void givenDatesAreNotCompleted_thenErrors() {
    form = getValidForm();

    form.setEarliestDateDay(null);
    form.setMostLikelyDateDay(null);
    form.setLatestDateDay(null);
    form.setEstimatedDateDay(null);

    assertThat(submissionEstimatesFormValidator.isValid(form, bindingResult))
        .isFalse();

    assertThat(bindingResult.getFieldErrors())
        .extracting(FieldError::getField, FieldError::getCode, FieldError::getDefaultMessage)
        .containsExactly(
            Tuple.tuple("earliestDateDay", "earliestDateDay.required", "Enter a complete earliest submission date"),

            Tuple.tuple("mostLikelyDateDay", "mostLikelyDateDay.required", "Enter a complete most likely submission date"),

            Tuple.tuple("latestDateDay", "latestDateDay.required", "Enter a complete latest submission date"),

            Tuple.tuple("estimatedDateDay", "estimatedDateDay.required", "Enter a complete date these were the estimated submission dates")
        );
  }

  private SubmissionEstimatesForm getInvalidForm() {
    var form = new SubmissionEstimatesForm();

    form.setEarliestDateDay("not a date");
    form.setEarliestDateMonth("not a date");
    form.setEarliestDateYear("not a date");

    form.setMostLikelyDateDay("not a date");
    form.setMostLikelyDateMonth("not a date");
    form.setMostLikelyDateYear("not a date");

    form.setLatestDateDay("not a date");
    form.setLatestDateMonth("not a date");
    form.setLatestDateYear("not a date");

    form.setEstimatedDateDay("not a date");
    form.setEstimatedDateMonth("not a date");
    form.setEstimatedDateYear("not a date");

    return form;
  }

  private SubmissionEstimatesForm getValidForm() {
    var form = new SubmissionEstimatesForm();

    form.setEarliestDateDay("3");
    form.setEarliestDateMonth("11");
    form.setEarliestDateYear("2023");

    form.setMostLikelyDateDay("12");
    form.setMostLikelyDateMonth("11");
    form.setMostLikelyDateYear("2023");

    form.setLatestDateDay("12");
    form.setLatestDateMonth("12");
    form.setLatestDateYear("2024");

    form.setEstimatedDateDay("08");
    form.setEstimatedDateMonth("03");
    form.setEstimatedDateYear("2023");

    return form;
  }
}