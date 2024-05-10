package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
  private BigDecimalUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static BigDecimal getValueOrZero(BigDecimal bigDecimal) {
    return bigDecimal == null
        ? BigDecimal.ZERO
        : bigDecimal;
  }

  public static boolean isValueZero(BigDecimal bigDecimal) {
    return bigDecimal.compareTo(BigDecimal.ZERO) == 0;
  }
}
