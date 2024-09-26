package uk.co.nstauthority.hydrocarbonmaturation.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
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
  void formatLongDate_localDate() {
    assertThat(DateUtil.formatLongDate(LocalDate.of(2012, 12, 1)))
        .isEqualTo("1 December 2012");
    assertThat(DateUtil.formatLongDate(LocalDate.of(2000, 1, 12)))
        .isEqualTo("12 January 2000");
    assertThat(DateUtil.formatLongDate(LocalDate.of(2055, 4, 22)))
        .isEqualTo("22 April 2055");
  }

  @Test
  void formatLongDate_instant() {
    assertThat(DateUtil.formatLongDate(Instant.parse("2012-12-01T00:00:00.00Z")))
        .isEqualTo("1 December 2012");
    assertThat(DateUtil.formatLongDate(Instant.parse("2000-01-12T00:00:00.00Z")))
        .isEqualTo("12 January 2000");
    assertThat(DateUtil.formatLongDate(Instant.parse("2055-04-22T00:00:00.00Z")))
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

  @Test
  void createLocalDate_validDateStrings() {
    assertThat(DateUtil.createLocalDate("2024", "4", "2"))
        .get()
        .isEqualTo(LocalDate.of(2024, 4, 2));
  }

  @Test
  void createLocalDate_nonNumericDay() {
    assertThat(DateUtil.createLocalDate("2024", "4", "a"))
        .isEmpty();
  }

  @Test
  void createLocalDate_nonNumericMonth() {
    assertThat(DateUtil.createLocalDate("2024", "a", "2"))
        .isEmpty();
  }

  @Test
  void createLocalDate_nonNumericYear() {
    assertThat(DateUtil.createLocalDate("a", "4", "2"))
        .isEmpty();
  }

  @Test
  void createLocalDate_emptyStrings() {
    assertThat(DateUtil.createLocalDate("", "", ""))
        .isEmpty();
  }

  @Test
  void createLocalDate_nullStrings() {
    assertThat(DateUtil.createLocalDate(null, null, null))
        .isEmpty();
  }

  @Test
  void findLatestItemForGivenStartDate() {
    var startDate1 = LocalDate.of(2012, 12, 1);
    var endDate1 = LocalDate.of(2012, 12, 10);
    var startDate2 = LocalDate.of(2012, 12, 11);
    var endDate2 = LocalDate.of(2012, 12, 20);
    var startDate3 = LocalDate.of(2012, 12, 21);
    var endDate3 = LocalDate.of(2012, 12, 30);

    var testDate = LocalDate.of(2012, 12, 15);

    var itemWithDates1 = new ItemWithStartAndEndDate("name", startDate1, endDate1);
    var itemWithDates2 = new ItemWithStartAndEndDate("name", startDate2, endDate2);
    var itemWithDates3 = new ItemWithStartAndEndDate("name", startDate3, endDate3);
    var itemsWithDates = List.of(itemWithDates1, itemWithDates2, itemWithDates3);

    var expectedItem = DateUtil.findLatestItemForGivenStartDate(
        itemsWithDates,
        testDate.atStartOfDay(),
        item -> item.startDate().atStartOfDay()
    );

    assertThat(expectedItem).isEqualTo(Optional.of(itemWithDates2));
  }

  private record ItemWithStartAndEndDate(String itemName, LocalDate startDate, LocalDate endDate) { }
}