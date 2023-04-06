package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NumberUtilTest {

  @Test
  void getNumberStringWithUnitsOrEmpty() {
    assertThat(NumberUtil.getNumberStringWithUnitsOrEmpty(BigDecimal.ONE, "£", "million"))
        .isEqualTo("£1 million");
    assertThat(NumberUtil.getNumberStringWithUnitsOrEmpty(BigDecimal.ZERO, "$", "psia"))
        .isEqualTo("$0 psia");
    assertThat(NumberUtil.getNumberStringWithUnitsOrEmpty(null, "£", "million"))
        .isEmpty();
  }

  @Test
  void getNumberStringWithUnitsWithoutSpaceOrEmpty() {
    assertThat(NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(BigDecimal.ONE, "million"))
        .isEqualTo("1million");
    assertThat(NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(BigDecimal.ZERO, "$"))
        .isEqualTo("0$");
    assertThat(NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(null, "£"))
        .isEmpty();
  }

  @Test
  void getNumberStringWithUnitsOrEmpty_withoutPrefix() {
    assertThat(NumberUtil.getNumberStringWithUnitsOrEmpty(10, "million"))
        .isEqualTo("10 million");
    assertThat(NumberUtil.getNumberStringWithUnitsOrEmpty(100, "psia"))
        .isEqualTo("100 psia");
    Integer nullInt = null;
    assertThat(NumberUtil.getNumberStringWithUnitsOrEmpty(nullInt, "million"))
        .isEmpty();
  }
}