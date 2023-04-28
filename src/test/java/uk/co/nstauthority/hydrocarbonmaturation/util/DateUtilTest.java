package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
  void formatLongDate() {
    assertThat(DateUtil.formatLongDate(LocalDate.of(2012, 12, 1)))
        .isEqualTo("1 December 2012");
    assertThat(DateUtil.formatLongDate(LocalDate.of(2000, 1, 12)))
        .isEqualTo("12 January 2000");
    assertThat(DateUtil.formatLongDate(LocalDate.of(2055, 4, 22)))
        .isEqualTo("22 April 2055");
  }

  @Test
  void formatDateOrBlank() {
    assertThat(DateUtil.formatDateOrBlank(LocalDate.of(2012, 12, 1)))
        .isEqualTo("1 Dec 2012");
    assertThat(DateUtil.formatDateOrBlank(null))
        .isEmpty();
  }

  @Test
  void formatInstantOrBlank() {
    var instant1 = LocalDateTime.of(2012, 10, 25, 20, 20)
        .toInstant(ZoneOffset.UTC);
    assertThat(DateUtil.formatInstantOrBlank(instant1)).isEqualTo("25 Oct 2012 09:20 BST");
    var instant2 = LocalDateTime.of(2015, 8, 22, 6, 20)
        .toInstant(ZoneOffset.UTC);
    assertThat(DateUtil.formatInstantOrBlank(instant2)).isEqualTo("22 Aug 2015 07:20 BST");
    var instant3 = LocalDateTime.of(2000, 1, 1, 9, 20)
        .toInstant(ZoneOffset.UTC);
    assertThat(DateUtil.formatInstantOrBlank(instant3)).isEqualTo("1 Jan 2000 09:20 GMT");
  }
}