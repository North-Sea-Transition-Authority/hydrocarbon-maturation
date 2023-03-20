package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class DateUtil {

  static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy");
  static final DateTimeFormatter DATE_FORMATTER_LONG_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy");

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
}