package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

public class SubmissionSummaryTestUtil {

  public static final List<SummaryKeyValue> keyValues =
      List.of(new SummaryKeyValue("k1", "v1"), new SummaryKeyValue("k2", "v2"));

  public static SummaryCard simpleSummaryCard = SummaryCard.simpleSummaryCard(new SummaryDataView(keyValues));

  public static SummarySection getGeneralDetailsSummarySection(List<SummaryItem> summaryItems) {
    return new SummarySection(GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER, summaryItems, "");
  }

  public static void assertSummarySection(SummarySection summarySection, int displayOrder) {
    assertThat(summarySection.displayOrder()).isEqualTo(displayOrder);
  }

  public static void assertSummaryItem(SummaryItem summaryItem,
                                       String displayName,
                                       int expectedSummaryCardCount) {
    assertThat(summaryItem.displayName()).isEqualTo(displayName);
    assertThat(summaryItem.summaryCards()).hasSize(expectedSummaryCardCount);
  }

  public static void assertSummaryCard(SummaryCard summaryCard,
                                       String displayName,
                                       SummaryCardType summaryCardType,
                                       Class<?> clazz) {
    assertThat(summaryCard.displayName()).isEqualTo(displayName);
    assertThat(summaryCard.summaryCardType()).isEqualTo(summaryCardType);
    assertThat(summaryCard.summaryData()).isInstanceOf(clazz);
  }

  public static void assertEmptySummaryCard(SummaryCard summaryCard) {
    assertThat(summaryCard.displayName()).isNull();
    assertThat(summaryCard.summaryCardType()).isEqualTo(SummaryCardType.EMPTY_SUMMARY);
    assertThat(summaryCard.summaryData()).isNull();
  }
}
