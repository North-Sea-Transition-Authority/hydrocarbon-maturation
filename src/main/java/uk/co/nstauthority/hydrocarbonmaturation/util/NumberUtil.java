package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class NumberUtil {

  private NumberUtil() {
    throw new IllegalUtilClassInstantiationException(NumberUtil.class);
  }

  public static String getNumberStringWithUnitsOrEmpty(BigDecimal number, String units) {
    return number != null
        ? number + " " + units
        : "";
  }
}
