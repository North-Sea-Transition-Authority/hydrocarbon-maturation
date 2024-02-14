package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class FieldStatusFormValidatorTest {
  private static final Field FIELD = new Field();

  private final LocalDate FIXED_TODAY = LocalDate.of(2024, 2, 1);
  private final Instant FIXED_TODAY_AS_INSTANT = Instant.parse("2024-02-01T00:00:00.00Z");

  @Mock
  private Clock clock;
  @Mock
  private FieldStatusHistoryService fieldStatusHistoryService;
  @InjectMocks
  private FieldStatusFormValidator fieldStatusFormValidator;


  private final LocalDate FIXED_PAST = LocalDate.of(2023, 2, 1);

  private FieldStatusForm form;
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    form = new FieldStatusForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void validateForm_validForm_thenNoErrors() {
    form = getValidForm();

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertTrue(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));
  }

  @Test
  void validateEmptyForm_thenErrors() {
    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatus", Set.of("fieldStatus.required")),
            entry("fieldStatusDateDay", Set.of("fieldStatusDateDay.required")),
            entry("fieldStatusDateMonth", Set.of("fieldStatusDateMonth.required")),
            entry("fieldStatusDateYear", Set.of("fieldStatusDateYear.required"))
        );
  }

  @Test
  void validateForm_incompleteDate_thenError() {
    form = getValidForm();

    form.setFieldStatusDateDay("03");
    form.setFieldStatusDateMonth("11");
    form.setFieldStatusDateYear("");

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatusDateYear", Set.of("fieldStatusDateYear.invalid"))
        );
  }

  @Test
  void validateForm_invalidDate_thenError() {
    form = getValidForm();

    form.setFieldStatusDateDay("a");
    form.setFieldStatusDateMonth("b");
    form.setFieldStatusDateYear("c");

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatusDateDay", Set.of("fieldStatusDateDay.invalid")),
            entry("fieldStatusDateMonth", Set.of("fieldStatusDateMonth.invalid")),
            entry("fieldStatusDateYear", Set.of("fieldStatusDateYear.invalid"))
        );
  }

  @Test
  void validateForm_dateInFuture_theError() {
    form = getValidForm();

    // date is after FIXED_INSTANT, so it is in future
    form.setFieldStatusDateDay("30");
    form.setFieldStatusDateMonth("01");
    form.setFieldStatusDateYear("2025");

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatusDateDay", Set.of("fieldStatusDate.dateInFuture"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = FieldStatus.class,
      names = {"STATUS_200", "STATUS_300", "STATUS_400", "STATUS_500", "STATUS_600", "STATUS_900"}
  )
  void validateForm_statusMustBeUniqueAndIsNot_thenError(FieldStatus fieldStatus) {
    form = getValidForm();
    form.setFieldStatus(fieldStatus);

    var existingStatusHistory = new FieldStatusHistory();
    existingStatusHistory.setFieldStatus(fieldStatus);
    existingStatusHistory.setStatusDate(FIXED_PAST);
    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(existingStatusHistory));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors)
        .containsExactly(
            entry("fieldStatus", Set.of("fieldStatus.invalidDuplicate"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = FieldStatus.class,
      names = {"STATUS_200", "STATUS_300", "STATUS_400", "STATUS_500", "STATUS_600"}
  )
  void validateForm_statusMustBeUniqueAndIs_thenNoError(FieldStatus fieldStatus) {
    form = getValidForm();
    form.setFieldStatus(fieldStatus);


    var existingStatusHistory = new FieldStatusHistory();
    existingStatusHistory.setFieldStatus(FieldStatus.STATUS_700);
    existingStatusHistory.setStatusDate(FIXED_TODAY.plusDays(1));
    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(existingStatusHistory));


    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertTrue(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).isEmpty();
  }

  @Test
  void validateForm_status900MustBeUniqueAndIs_thenNoError() {
    form = getValidForm();
    form.setFieldStatus(FieldStatus.STATUS_900);

    var existingStatusHistory = new FieldStatusHistory();
    existingStatusHistory.setFieldStatus(FieldStatus.STATUS_200);
    existingStatusHistory.setStatusDate(FIXED_PAST);
    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(existingStatusHistory));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertTrue(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = FieldStatus.class,
      names = {"STATUS_700", "STATUS_799", "STATUS_800"}
  )
  void validateForm_statusDoesNotNeedToBeUnique_thenNoError(FieldStatus fieldStatus) {
    form = getValidForm();
    form.setFieldStatus(fieldStatus);

    var existingStatusHistory1 = new FieldStatusHistory();
    existingStatusHistory1.setFieldStatus(fieldStatus);
    existingStatusHistory1.setStatusDate(FIXED_PAST);

    var existingStatusHistory2 = new FieldStatusHistory();
    existingStatusHistory2.setFieldStatus(FieldStatus.STATUS_899);
    existingStatusHistory2.setStatusDate(FIXED_PAST);

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(existingStatusHistory1));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertTrue(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).isEmpty();
  }

  @Test
  void validateForm_status899DoesNotNeedToBeUnique_thenNoError() {
    var fieldStatus = FieldStatus.STATUS_899;

    form = getValidForm();
    form.setFieldStatus(fieldStatus);

    var existingStatusHistory1 = new FieldStatusHistory();
    existingStatusHistory1.setFieldStatus(fieldStatus);
    existingStatusHistory1.setStatusDate(FIXED_PAST);

    var existingStatusHistory2 = new FieldStatusHistory();
    existingStatusHistory2.setFieldStatus(FieldStatus.STATUS_700);
    existingStatusHistory2.setStatusDate(FIXED_PAST);

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(existingStatusHistory1));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertTrue(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).isEmpty();
  }

  @Test
  void validateForm_newDateIsAfterExistingStatus900_thenError() {
    form = getValidForm();
    form.setFieldStatus(FieldStatus.STATUS_500);

    var existingStatusHistory = new FieldStatusHistory();

    existingStatusHistory.setFieldStatus(FieldStatus.STATUS_900);
    existingStatusHistory.setStatusDate(FIXED_PAST);

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(existingStatusHistory));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).containsExactly(
        entry("fieldStatusDateDay", Set.of("fieldStatusDate.invalidOrder"))
    );
  }

  @Test
  void validateForm_newStatusBreaksOrderForStatuses600OrLess_thenError() {
    form = getValidForm();
    form.setFieldStatus(FieldStatus.STATUS_500);

    var invalidLocalDate = FIXED_PAST.plusDays(1);
    form.setFieldStatusDateDay(String.valueOf(invalidLocalDate.getDayOfMonth()));
    form.setFieldStatusDateMonth(String.valueOf(invalidLocalDate.getMonthValue()));
    form.setFieldStatusDateYear(String.valueOf(invalidLocalDate.getYear()));

    var esh1 = new FieldStatusHistory();
    esh1.setFieldStatus(FieldStatus.STATUS_200);
    esh1.setStatusDate(FIXED_PAST);

    var esh2 = new FieldStatusHistory();
    esh2.setFieldStatus(FieldStatus.STATUS_400);
    esh2.setStatusDate(FIXED_PAST.plusDays(2));

    var esh3 = new FieldStatusHistory();
    esh3.setFieldStatus(FieldStatus.STATUS_600);
    esh3.setStatusDate(FIXED_PAST.plusDays(4));

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(esh1, esh2, esh3));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).containsExactly(
        entry("fieldStatusDateDay", Set.of("fieldStatusDate.invalidOrder"))
    );
  }

  @Test
  void validateForm_newStatusDoesNotBreakOrderForStatuses600OrLess_thenNoError() {
    form = getValidForm();
    form.setFieldStatus(FieldStatus.STATUS_500);

    var validLocalDate = FIXED_PAST.plusDays(3);
    form.setFieldStatusDateDay(String.valueOf(validLocalDate.getDayOfMonth()));
    form.setFieldStatusDateMonth(String.valueOf(validLocalDate.getMonthValue()));
    form.setFieldStatusDateYear(String.valueOf(validLocalDate.getYear()));

    var esh1 = new FieldStatusHistory();
    esh1.setFieldStatus(FieldStatus.STATUS_200);
    esh1.setStatusDate(FIXED_PAST);

    var esh2 = new FieldStatusHistory();
    esh2.setFieldStatus(FieldStatus.STATUS_400);
    esh2.setStatusDate(FIXED_PAST.plusDays(2));

    var esh3 = new FieldStatusHistory();
    esh3.setFieldStatus(FieldStatus.STATUS_600);
    esh3.setStatusDate(FIXED_PAST.plusDays(4));

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(esh1, esh2, esh3));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    fieldStatusFormValidator.isValid(form, bindingResult, FIELD);

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors).isEmpty();
  }

  @Test
  void validateForm_statusIs700OrMoreOnSameDateAsOtherStatus700OrMore_thenError() {
    form = getValidForm();
    form.setFieldStatus(FieldStatus.STATUS_899);

    var invalidLocalDate = FIXED_PAST;
    form.setFieldStatusDateDay(String.valueOf(invalidLocalDate.getDayOfMonth()));
    form.setFieldStatusDateMonth(String.valueOf(invalidLocalDate.getMonthValue()));
    form.setFieldStatusDateYear(String.valueOf(invalidLocalDate.getYear()));

    var esh1 = new FieldStatusHistory();
    esh1.setFieldStatus(FieldStatus.STATUS_700);
    esh1.setStatusDate(FIXED_PAST);

    var esh2 = new FieldStatusHistory();
    esh2.setFieldStatus(FieldStatus.STATUS_800);
    esh2.setStatusDate(FIXED_PAST.plusDays(2));

    var esh3 = new FieldStatusHistory();
    esh3.setFieldStatus(FieldStatus.STATUS_900);
    esh3.setStatusDate(FIXED_PAST.plusDays(4));

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(esh1, esh2, esh3));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    assertFalse(fieldStatusFormValidator.isValid(form, bindingResult, FIELD));

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(errors).containsExactly(
        entry("fieldStatusDateDay", Set.of("fieldStatusDate.invalid"))
    );
  }

  @Test
  void validateForm_statusIs700OrMoreOnDifferentDateThanAnyOtherStatus700OrMore_thenNoError() {
    form = getValidForm();
    form.setFieldStatus(FieldStatus.STATUS_700);

    var validLocalDate = FIXED_PAST.plusDays(3);
    form.setFieldStatusDateDay(String.valueOf(validLocalDate.getDayOfMonth()));
    form.setFieldStatusDateMonth(String.valueOf(validLocalDate.getMonthValue()));
    form.setFieldStatusDateYear(String.valueOf(validLocalDate.getYear()));

    var esh1 = new FieldStatusHistory();
    esh1.setFieldStatus(FieldStatus.STATUS_700);
    esh1.setStatusDate(FIXED_PAST);

    var esh2 = new FieldStatusHistory();
    esh2.setFieldStatus(FieldStatus.STATUS_799);
    esh2.setStatusDate(FIXED_PAST.plusDays(2));

    var esh3 = new FieldStatusHistory();
    esh3.setFieldStatus(FieldStatus.STATUS_900);
    esh3.setStatusDate(FIXED_PAST.plusDays(4));

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(esh1, esh2, esh3));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    fieldStatusFormValidator.isValid(form, bindingResult, FIELD);

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors).isEmpty();
  }

  @Test
  void validateForm_statusIs700OrMoreAndBeforeStatus600OrLess_thenError() {
    form = getValidForm();
    form.setFieldStatus(FieldStatus.STATUS_800);

    var validLocalDate = FIXED_PAST.plusDays(1);
    form.setFieldStatusDateDay(String.valueOf(validLocalDate.getDayOfMonth()));
    form.setFieldStatusDateMonth(String.valueOf(validLocalDate.getMonthValue()));
    form.setFieldStatusDateYear(String.valueOf(validLocalDate.getYear()));

    var esh1 = new FieldStatusHistory();
    esh1.setFieldStatus(FieldStatus.STATUS_400);
    esh1.setStatusDate(FIXED_PAST);

    var esh2 = new FieldStatusHistory();
    esh2.setFieldStatus(FieldStatus.STATUS_600);
    esh2.setStatusDate(FIXED_PAST.plusDays(2));

    var esh3 = new FieldStatusHistory();
    esh3.setFieldStatus(FieldStatus.STATUS_700);
    esh3.setStatusDate(FIXED_PAST.plusDays(4));

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(FIELD))
        .thenReturn(List.of(esh1, esh2, esh3));

    when(clock.instant()).thenReturn(FIXED_TODAY_AS_INSTANT);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    fieldStatusFormValidator.isValid(form, bindingResult, FIELD);

    var errors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(errors).containsExactly(
        entry("fieldStatusDateDay", Set.of("fieldStatusDate.invalidOrder"))
    );
  }


  private FieldStatusForm getValidForm() {
    var form = new FieldStatusForm();

    form.setFieldStatus(FieldStatus.STATUS_300);
    form.setFieldStatusDateDay("03");
    form.setFieldStatusDateMonth("11");
    form.setFieldStatusDateYear("2023");

    return form;
  }
}