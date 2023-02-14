package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateUtilTest {

  @Test
  void formatDate() {
    assertThat(DateUtil.formatDate(LocalDate.of(2012, 12, 1)))
        .isEqualTo("1 Dec 2012");
    assertThat(DateUtil.formatDate(LocalDate.of(2000, 1, 12)))
        .isEqualTo("12 Jan 2000");
    assertThat(DateUtil.formatDate(LocalDate.of(2055, 4, 22)))
        .isEqualTo("22 Apr 2055");
  }

  @Test
  void formatDateOrBlank() {
    assertThat(DateUtil.formatDateOrBlank(LocalDate.of(2012, 12, 1)))
        .isEqualTo("1 Dec 2012");
    assertThat(DateUtil.formatDateOrBlank(null))
        .isEmpty();
  }
}