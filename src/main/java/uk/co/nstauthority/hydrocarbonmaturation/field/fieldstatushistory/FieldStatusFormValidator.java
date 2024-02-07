package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
public class FieldStatusFormValidator {

  public static final String FIELD_STATUS_ERROR_LOCATION = "fieldStatus";

  public static final String FIELD_STATUS_DATE_ERROR_LOCATION = "fieldStatusDateDay";
  public static final String FIELD_STATUS_DATE_INVALID_DATE = "fieldStatusDate.invalid";
  public static final String FIELD_STATUS_DATE_INVALID_ORDER = "fieldStatusDate.invalidOrder";

  private final FieldStatusHistoryService fieldStatusHistoryService;
  private final Clock clock;

  @Autowired
  public FieldStatusFormValidator(FieldStatusHistoryService fieldStatusHistoryService, Clock clock) {
    this.fieldStatusHistoryService = fieldStatusHistoryService;
    this.clock = clock;
  }

  public boolean isValid(FieldStatusForm form,
                         Errors errors,
                         Field field) {
    if (isValidForm(errors, form)) {
      validateFieldStatusHistoryRules(errors, form, field);
    }
    return !errors.hasErrors();
  }

  private static boolean isValidForm(@NotNull Errors errors, FieldStatusForm form) {
    if (form.getFieldStatus() == null) {
      errors.rejectValue(
          FIELD_STATUS_ERROR_LOCATION,
          "fieldStatus.required",
          "Select a field status"
      );
    }

    DateValidationUtil.validateDate(
        "fieldStatusDate",
        "date the field entered status",
        form.getFieldStatusDateDay(),
        form.getFieldStatusDateMonth(),
        form.getFieldStatusDateYear(),
        errors
    );

    return !errors.hasErrors();
  }

  private void validateFieldStatusHistoryRules(Errors errors, FieldStatusForm form, Field field) {
    var fullFieldStatusHistory = fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(field);

    var newFieldStatusHistory = new FieldStatusHistory();
    newFieldStatusHistory.setFieldStatus(form.getFieldStatus());
    newFieldStatusHistory.setStatusDate(
        LocalDate.of(
            Integer.parseInt(form.getFieldStatusDateYear()),
            Integer.parseInt(form.getFieldStatusDateMonth()),
            Integer.parseInt(form.getFieldStatusDateDay())
        ).atStartOfDay(ZoneId.systemDefault()).toInstant()
    );

    if (newFieldStatusHistory.getStatusDate().isAfter(clock.instant())) {
      errors.rejectValue(
          FIELD_STATUS_DATE_ERROR_LOCATION,
          "fieldStatusDate.dateInFuture",
          "Date field entered status cannot be in the future"
      );
    } else if (violatesUniqueConstraint(newFieldStatusHistory, fullFieldStatusHistory)) {
      errors.rejectValue(
          FIELD_STATUS_ERROR_LOCATION,
          "fieldStatus.invalidDuplicate",
          "Field already has a date the field entered status %s"
              .formatted(newFieldStatusHistory.getFieldStatus().getDisplayName())
      );
    } else if (newIsAfterStatus900(newFieldStatusHistory, fullFieldStatusHistory)) {
      errors.rejectValue(
          FIELD_STATUS_DATE_ERROR_LOCATION,
          FIELD_STATUS_DATE_INVALID_ORDER,
          "Field has already entered status %s before this date"
              .formatted(FieldStatus.STATUS_900.getDisplayName())
      );
    } else if (newFieldStatusHistory.getFieldStatus().getStatusValue() <= 600
        && !isValidOrderingForStatuses600OrLess(newFieldStatusHistory, fullFieldStatusHistory)) {
      errors.rejectValue(
          FIELD_STATUS_DATE_ERROR_LOCATION,
          FIELD_STATUS_DATE_INVALID_ORDER,
          "Field status must be increasing for statuses between 200 and 600"
      );
    } else if (newFieldStatusHistory.getFieldStatus().getStatusValue() >= 700) {
      if (!isValidOrderingForStatuses700OrMore(newFieldStatusHistory, fullFieldStatusHistory)) {
        errors.rejectValue(
            FIELD_STATUS_DATE_ERROR_LOCATION,
            FIELD_STATUS_DATE_INVALID_DATE,
            "The field has already entered a status between 700 and 900 on that date"
        );
      } else if (newStatusIsBeforeMostRecent600OrLess(newFieldStatusHistory, fullFieldStatusHistory)) {
        errors.rejectValue(
            FIELD_STATUS_DATE_ERROR_LOCATION,
            FIELD_STATUS_DATE_INVALID_ORDER,
            "Date the field entered status %s must be after any dates the field entered a status between 200 and 600"
                .formatted(newFieldStatusHistory.getFieldStatus().getDisplayName())
        );
      }
    }
  }

  /*
   * Check the new status maintains the order of any statuses greater than or equal to 700
   * i.e. they can be in any order but not on the same date
   */
  private static boolean isValidOrderingForStatuses700OrMore(FieldStatusHistory newFieldStatusHistory,
                                                             List<FieldStatusHistory> fullFieldStatusHistory) {
    return fullFieldStatusHistory.stream()
        .filter(fieldStatusHistory -> fieldStatusHistory.getFieldStatus().getStatusValue() >= 700)
        .noneMatch(fieldStatusHistory ->
            fieldStatusHistory.getStatusDate().equals(newFieldStatusHistory.getStatusDate()));
  }

  private static boolean newStatusIsBeforeMostRecent600OrLess(FieldStatusHistory newFieldStatusHistory,
                                                              List<FieldStatusHistory> fullFieldStatusHistory) {
    var maxDateLess = getMostRecentDateForStatuses600OrLess(newFieldStatusHistory, fullFieldStatusHistory);
    return newFieldStatusHistory.getStatusDate().isBefore(maxDateLess);
  }

  private static Instant getMostRecentDateForStatuses600OrLess(FieldStatusHistory newFieldStatusHistory,
                                                               List<FieldStatusHistory> fullFieldStatusHistory) {
    return fullFieldStatusHistory.stream()
        .filter(history -> history.getFieldStatus().getStatusValue() <= 600)
        .map(FieldStatusHistory::getStatusDate)
        .max(Comparator.naturalOrder())
        .orElse(newFieldStatusHistory.getStatusDate().minus(1, ChronoUnit.DAYS));
  }

  /*
   * Check the new status maintains the order of any statuses less than or equal to 600
   * i.e. they must go up sequentially by status and date (where the dates can be equal)
   */
  private static boolean isValidOrderingForStatuses600OrLess(FieldStatusHistory newFieldStatusHistory,
                                                             List<FieldStatusHistory> fullFieldStatusHistory) {
    return fullFieldStatusHistory.stream()
        .filter(fieldStatusHistory -> fieldStatusHistory.getFieldStatus().getStatusValue() <= 600)
        .allMatch(fieldStatusHistory -> newStatusIsGreaterAndComesAfter(newFieldStatusHistory, fieldStatusHistory)
            || newStatusIsLowerAndComesBefore(newFieldStatusHistory, fieldStatusHistory));
  }

  private static boolean newStatusIsLowerAndComesBefore(FieldStatusHistory newFieldStatusHistory,
                                                        FieldStatusHistory fieldStatusHistory) {
    return fieldStatusHistory.getFieldStatus().getStatusValue() > newFieldStatusHistory.getFieldStatus().getStatusValue()
        && (fieldStatusHistory.getStatusDate().isAfter(newFieldStatusHistory.getStatusDate())
        || fieldStatusHistory.getStatusDate().equals(newFieldStatusHistory.getStatusDate()));
  }

  private static boolean newStatusIsGreaterAndComesAfter(FieldStatusHistory newFieldStatusHistory,
                                                         FieldStatusHistory fieldStatusHistory) {
    return fieldStatusHistory.getFieldStatus().getStatusValue() < newFieldStatusHistory.getFieldStatus().getStatusValue()
        && (fieldStatusHistory.getStatusDate().isBefore(newFieldStatusHistory.getStatusDate())
        || fieldStatusHistory.getStatusDate().equals(newFieldStatusHistory.getStatusDate()));
  }

  private boolean newIsAfterStatus900(FieldStatusHistory newFieldStatusHistory, List<FieldStatusHistory> fullFieldStatusHistory) {
    for (var fieldStatusHistory : fullFieldStatusHistory) {
      if (FieldStatus.STATUS_900.equals(fieldStatusHistory.getFieldStatus())
          && newFieldStatusHistory.getStatusDate().isAfter(fieldStatusHistory.getStatusDate())) {
        return true;
      }
    }
    return false;
  }

  private boolean violatesUniqueConstraint(FieldStatusHistory newFieldStatusHistory,
                                           List<FieldStatusHistory> existingHistories) {
    return switch (newFieldStatusHistory.getFieldStatus()) {
      case STATUS_200, STATUS_300, STATUS_400, STATUS_500, STATUS_600, STATUS_900 ->
          statusAlreadyExists(newFieldStatusHistory.getFieldStatus(), existingHistories);
      case STATUS_700, STATUS_799, STATUS_800, STATUS_899 -> false;
    };
  }

  private boolean statusAlreadyExists(FieldStatus fieldStatus, List<FieldStatusHistory> existingHistories) {
    return existingHistories.stream()
        .map(FieldStatusHistory::getFieldStatus)
        .collect(Collectors.toSet())
        .contains(fieldStatus);
  }
}

