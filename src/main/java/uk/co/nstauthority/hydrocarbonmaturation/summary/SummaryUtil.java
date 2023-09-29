package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.math.BigDecimal;
import java.time.LocalDate;
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
      return DateUtil.formatDate(localDateValue);
    } else if (value instanceof BigDecimal bigDecimalValue) {
      return DecimalFormatUtil.bigDecimalToFormattedString(bigDecimalValue);
    } else {
      throw new SummaryException("Unexpected value class type: " + value.getClass().getName());
    }
  }
}