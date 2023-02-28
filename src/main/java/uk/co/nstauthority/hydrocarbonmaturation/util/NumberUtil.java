package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class NumberUtil {

  private NumberUtil() {
    throw new IllegalUtilClassInstantiationException(NumberUtil.class);
  }

  public static String getNumberStringWithUnitsOrEmpty(BigDecimal number,
                                                       String unitsPrefix,
                                                       String unitsSuffix) {
    return number != null
        ? unitsPrefix + number + " " + unitsSuffix
        : "";
  }

  public static String getNumberStringWithUnitsWithoutSpaceOrEmpty(BigDecimal number,
                                                                   String units) {
    return number != null
        ? number + units
        : "";
  }

  public static String getNumberStringWithUnitsOrEmpty(BigDecimal number, String units) {
    return number != null
        ? number + " " + units
        : "";
  }

  public static String getNumberStringWithUnitsOrEmpty(Integer number, String units) {
    return number != null
        ? number + " " + units
        : "";
  }

  public static List<String> toListOfStrings(Collection<Integer> integerCollection) {
    return integerCollection.stream()
        .map(String::valueOf)
        .toList();
  }

  public static List<Integer> toListOfIntegers(Collection<String> stringCollection) {
    return stringCollection.stream()
        .map(Integer::parseInt)
        .toList();
  }
}
