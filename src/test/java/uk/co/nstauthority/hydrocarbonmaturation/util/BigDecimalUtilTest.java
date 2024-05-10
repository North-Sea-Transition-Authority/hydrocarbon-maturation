package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BigDecimalUtilTest {

  @Test
  void getValueOrZero_returnsValue() {
    assertThat(BigDecimalUtil.getValueOrZero(BigDecimal.TEN))
        .isEqualTo(BigDecimal.TEN);
  }

  @Test
  void getValueOrZero_returnsZero() {
    assertThat(BigDecimalUtil.getValueOrZero(null))
        .isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void isValueZero() {
    assertThat(BigDecimalUtil.isValueZero(BigDecimal.ZERO))
        .isTrue();
  }

  @Test
  void isValueZero_isNot() {
    assertThat(BigDecimalUtil.isValueZero(BigDecimal.TEN))
        .isFalse();
  }
}
