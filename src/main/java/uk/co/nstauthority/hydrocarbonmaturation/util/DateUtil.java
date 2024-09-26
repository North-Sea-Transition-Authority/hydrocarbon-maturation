package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class DateUtil {

  static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy");
  static final DateTimeFormatter DATE_FORMATTER_LONG_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy");
  static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm z")
      .withZone(ZoneId.of("Europe/London"));

  private DateUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  /**
   * Produce a GDS-complaint long date string for the provided date.
   *
   * @param localDate The LocalDate to format
   * @return The formatted temporal in a GDS-complaint format
   */
  public static String formatLongDate(LocalDate localDate) {
    return DATE_FORMATTER_LONG_DATE.format(localDate);
  }

  /**
   * Produce a GDS-complaint long date string for the provided date.
   *
   * @param instant The Instant to format
   * @return The formatted temporal in a GDS-complaint format
   */
  public static String formatLongDate(Instant instant) {
    var localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());
    return DATE_FORMATTER_LONG_DATE.format(localDate);
  }

  /**
   * Produce a GDS-complaint string for the provided date.
   *
   * @param localDate The LocalDate to format
   * @return The formatted temporal in a GDS-complaint format
   */
  public static String formatDate(LocalDate localDate) {
    return DATE_FORMATTER.format(localDate);
  }

  /**
   * Produce a GDS-complaint string for the provided date or an empty string when handling a null value.
   *
   * @param localDate The LocalDate to format
   * @return The formatted temporal in a GDS-complaint format or empty string
   */
  public static String formatDateOrBlank(LocalDate localDate) {
    if (localDate != null) {
      return DATE_FORMATTER.format(localDate);
    } else {
      return "";
    }
  }

  /**
   * Produce a GDS-complaint string for the provided date or an empty string when handling a null value.
   *
   * @param localDate The LocalDate to format
   * @return The formatted temporal in a GDS-complaint format or empty string
   */
  public static String formatLongDateOrBlank(LocalDate localDate) {
    if (localDate != null) {
      return DATE_FORMATTER_LONG_DATE.format(localDate);
    } else {
      return "";
    }
  }

  /**
   * Produce a displayable string for the provided instant or an empty string when handling a null value.
   *
   * @param instant The LocalDate to format
   * @return The formatted temporal in a GDS-complaint format or empty string
   */
  public static String formatInstantOrBlank(Instant instant) {
    if (instant != null) {
      return DATE_TIME_FORMATTER.format(instant);
    } else {
      return "";
    }
  }

  /**
   * Produce an optional containing a local date from some string values or
   * empty if one could not be created.
   * @param yearValue the year e.g. 2024
   * @param monthValue the numeric month e.g. 3 (March)
   * @param dayOfMonthValue the numeric day of the month e.g. 15 (15th of the month)
   * @return optional of local date or empty if one cannot be created from the inputs
   */
  public static Optional<LocalDate> createLocalDate(String yearValue, String monthValue, String dayOfMonthValue) {
    try {
      return Optional.of(
          LocalDate.of(
              Integer.parseInt(yearValue),
              Integer.parseInt(monthValue),
              Integer.parseInt(dayOfMonthValue))
      );
    } catch (NumberFormatException | DateTimeException e) {
      return Optional.empty();
    }
  }

  public static <T> Optional<T> findLatestItemForGivenStartDate(
      Collection<T> collectionWithDates,
      LocalDateTime startDateToFind,
      Function<T, LocalDateTime> startDateExtractor
  ) {
    return collectionWithDates.stream()
        .filter(dateItem -> startDateExtractor.apply(dateItem).compareTo(startDateToFind) <= 0)
        .max(Comparator.comparing(startDateExtractor));
  }
}