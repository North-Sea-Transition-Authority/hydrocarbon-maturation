package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;

class SummaryUtil {
  private SummaryUtil() {
    throw new IllegalStateException("Utility class");
  }

  static String format(Object value) {
    if (value == null) {
      return null;
    } else if (value instanceof String stringValue) {
      return stringValue;
    } else if (value instanceof Integer integerValue) {
      return String.valueOf(integerValue);
    } else if (value instanceof Boolean booleanValue) {
      return BooleanUtil.yesNoFromBoolean(booleanValue);
    } else if (value instanceof LocalDate localDateValue) {
      return DateUtil.formatLongDate(localDateValue);
    } else if (value instanceof BigDecimal bigDecimalValue) {
      return DecimalFormatUtil.bigDecimalToFormattedString(bigDecimalValue);
    } else if (value instanceof DecimalInput decimalInput) {
      return decimalInput.getDecimalInputValue();
    } else if (value instanceof Displayable displayable) {
      return DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(displayable);
    } else {
      throw new SummaryException("Unexpected value class type: " + value.getClass().getName());
    }
  }

  private static SummaryTableValue generateSummaryTableValue(Object value) {
    return new SummaryTableValue(value, null, List.of());
  }

  static SummaryTableValue formatForSummaryTableValue(Object value) {
    return value instanceof SummaryTableValue summaryTableValue
        ? summaryTableValue
        : generateSummaryTableValue(value);
  }
}