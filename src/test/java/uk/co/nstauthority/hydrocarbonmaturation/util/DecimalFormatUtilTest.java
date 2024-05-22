package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DecimalFormatUtilTest {

  @Test
  void doubleToFormattedString() {
    assertThat(DecimalFormatUtil.doubleToFormattedString(12.89984928482482948))
        .isEqualTo("12.8998492848");
  }

  @Test
  void doubleToFormattedString_null() {
    assertThat(DecimalFormatUtil.doubleToFormattedString(null))
        .isEmpty();
  }

  @Test
  void bigDecimalToTwoDecimalPlace() {
    assertThat(DecimalFormatUtil.bigDecimalToTwoDecimalPlace(BigDecimal.valueOf(192.19092)))
        .isEqualTo("192.19");
  }

  @Test
  void bigDecimalToTwoDecimalPlace_null() {
    assertThat(DecimalFormatUtil.bigDecimalToTwoDecimalPlace(null))
        .isEmpty();
  }
  @Test
  void bigDecimalToFormattedString() {
    assertThat(DecimalFormatUtil.bigDecimalToFormattedString(BigDecimal.valueOf(12.10293928219191)))
        .isEqualTo("12.1029392822");
  }

  @Test
  void bigDecimalToFormattedString_null() {
    assertThat(DecimalFormatUtil.bigDecimalToFormattedString(null))
        .isEmpty();
  }
}
