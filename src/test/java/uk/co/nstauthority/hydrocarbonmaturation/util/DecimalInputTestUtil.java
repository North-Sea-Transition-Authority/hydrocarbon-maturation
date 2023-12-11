package uk.co.nstauthority.hydrocarbonmaturation.util;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

public class DecimalInputTestUtil {

  private DecimalInputTestUtil() {
    throw new IllegalStateException("DecimalInputUtil is a util class and should not be instantiated");
  }

  public static DecimalInput getDecimalInput(int input) {
    return DecimalInput.createDecimalInput(BigDecimal.valueOf(input));
  }

  public static Boolean decimalInputEqualsBigDecimal(DecimalInput decimalInput, BigDecimal bigDecimal) {
    var decimalInputAsBigDecimal = decimalInput.asBigDecimal();

    if(decimalInputAsBigDecimal.isPresent()) {
      return decimalInputAsBigDecimal.get().compareTo(bigDecimal) == 0;
    }
    return null;
  }
}
