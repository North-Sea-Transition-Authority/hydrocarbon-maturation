package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
class SubmissionEstimatesFormValidator {
  public static final String EARLIEST_DATE = "earliestDate";
  public static final String MOST_LIKELY_DATE = "mostLikelyDate";
  public static final String LATEST_DATE = "latestDate";
  public static final String ESTIMATED_DATE = "estimatedDate";

  boolean isValid(SubmissionEstimatesForm form, Errors errors) {
    var isValidEarliest = DateValidationUtil.validateOptionalDate(
        EARLIEST_DATE,
        "earliest submission date",
        form.getEarliestDateDay(),
        form.getEarliestDateMonth(),
        form.getEarliestDateYear(),
        errors
    );

    var isValidMostLikely = DateValidationUtil.validateDate(
        MOST_LIKELY_DATE,
        "most likely submission date",
        form.getMostLikelyDateDay(),
        form.getMostLikelyDateMonth(),
        form.getMostLikelyDateYear(),
        errors
    );

    var isValidLatest = DateValidationUtil.validateOptionalDate(
        LATEST_DATE,
        "latest submission date",
        form.getLatestDateDay(),
        form.getLatestDateMonth(),
        form.getLatestDateYear(),
        errors
    );

    var isValidEstimated = DateValidationUtil.validateDate(
        ESTIMATED_DATE,
        "date these were the estimated submission dates",
        form.getEstimatedDateDay(),
        form.getEstimatedDateMonth(),
        form.getEstimatedDateYear(),
        errors
    );

    validateEarliestAndMostLikelyOrder(form, errors, isValidEarliest, isValidMostLikely);
    validateLatestDateOrder(form, errors, isValidLatest, isValidEarliest, isValidMostLikely);
    validateEstimatedTodayOrInPast(form, errors, isValidEstimated);

    return !errors.hasErrors();
  }

  private void validateEstimatedTodayOrInPast(SubmissionEstimatesForm form, Errors errors, boolean isValidEstimated) {
    if (isValidEstimated) {
      DateValidationUtil.validateDateIsTodayOrInThePast(
          ESTIMATED_DATE,
          "estimated date",
          form.getEstimatedDateDay(),
          form.getEstimatedDateMonth(),
          form.getEstimatedDateYear(),
          errors
      );
    }
  }

  private void validateEarliestAndMostLikelyOrder(SubmissionEstimatesForm form,
                                                  Errors errors,
                                                  boolean isValidEarliest,
                                                  boolean isValidMostLikely) {
    if (isValidEarliest && isValidMostLikely) {
      var earliestDate = getEarliestDate(form);
      var mostLikelyDate = getMostLikelyDate(form);

      if (earliestDate.isEmpty() || mostLikelyDate.isEmpty()) {
        return;
      }

      DateValidationUtil.validateDateIsOnOrBeforeComparisonDate(
          EARLIEST_DATE,
          "earliest date",
          earliestDate.get(),
          mostLikelyDate.get(),
          "on or before the most likely date",
          errors
      );
    }
  }

  private void validateLatestDateOrder(SubmissionEstimatesForm form,
                                       Errors errors,
                                       boolean isValidLatest,
                                       boolean isValidEarliest,
                                       boolean isValidMostLikely) {
    if (isValidLatest) {
      var latestDate = getLatestDate(form);
      if (latestDate.isEmpty()) {
        return;
      }

      if (isValidEarliest) {
        var earliestDate = getEarliestDate(form);
        if (earliestDate.isEmpty()) {
          return;
        }

        DateValidationUtil.validateDateIsOnOrBeforeComparisonDate(
            EARLIEST_DATE,
            "earliest date",
            earliestDate.get(),
            latestDate.get(),
            "on or before the latest date",
            errors
        );
      }

      if (isValidMostLikely) {
        var mostLikelyDate = getMostLikelyDate(form);
        if (mostLikelyDate.isEmpty()) {
          return;
        }

        DateValidationUtil.validateDateIsOnOrBeforeComparisonDate(
            MOST_LIKELY_DATE,
            "most likely date",
            mostLikelyDate.get(),
            latestDate.get(),
            "on or before the latest date",
            errors
        );
      }
    }
  }

  private Optional<LocalDate> getMostLikelyDate(SubmissionEstimatesForm form) {
    try {
      return Optional.of(LocalDate.of(
          Integer.parseInt(form.getMostLikelyDateYear()),
          Integer.parseInt(form.getMostLikelyDateMonth()),
          Integer.parseInt(form.getMostLikelyDateDay()))
      );
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private Optional<LocalDate> getLatestDate(SubmissionEstimatesForm form) {
    try {
      return Optional.of(LocalDate.of(
          Integer.parseInt(form.getLatestDateYear()),
          Integer.parseInt(form.getLatestDateMonth()),
          Integer.parseInt(form.getLatestDateDay()))
      );
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private Optional<LocalDate> getEarliestDate(SubmissionEstimatesForm form) {
    try {
      return Optional.of(LocalDate.of(
          Integer.parseInt(form.getEarliestDateYear()),
          Integer.parseInt(form.getEarliestDateMonth()),
          Integer.parseInt(form.getEarliestDateDay()))
      );
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
