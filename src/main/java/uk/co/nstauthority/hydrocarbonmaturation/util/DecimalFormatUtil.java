package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DecimalFormatUtil {

  public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#.##########");

  private DecimalFormatUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String doubleToFormattedString(Double value) {
    return value != null ? DEFAULT_DECIMAL_FORMAT.format(value) : "";
  }

  public static String bigDecimalToFormattedString(BigDecimal value) {
    return value != null ? DEFAULT_DECIMAL_FORMAT.format(value) : "";
  }
}
