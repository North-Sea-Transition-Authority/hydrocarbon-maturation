package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;

public record SummaryCard(
    String displayName,
    SummaryCardType summaryCardType,
    Object summaryData
) {

  public static SummaryCard simpleSummaryCardWithHeading(String displayName,
                                                         SummaryDataView summaryData) {
    return new SummaryCard(
        displayName,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryData
    );
  }

  public static SummaryCard simpleSummaryCard(SummaryDataView summaryData) {
    return simpleSummaryCardWithHeading(null, summaryData);
  }

  public static SummaryCard emptySummaryCard() {
    return new SummaryCard(
        null,
        SummaryCardType.EMPTY_SUMMARY,
        null
    );
  }

  public static SummaryCard emptySummaryCardWithHeading(String heading) {
    return new SummaryCard(
        heading,
        SummaryCardType.EMPTY_SUMMARY,
        null
    );
  }

  public static List<SummaryCard> emptySummaryCardList() {
    return List.of(emptySummaryCard());
  }

  public static SummaryCard tableSummaryCardWithHeading(String displayName,
                                                        SummaryTableView summaryData) {
    return new SummaryCard(
        displayName,
        SummaryCardType.TABLE_SUMMARY,
        summaryData
    );
  }

  public static SummaryCard tableSummaryCard(SummaryTableView summaryData) {
    return tableSummaryCardWithHeading(null, summaryData);
  }

  public static SummaryCard probabilitySummaryCardWithHeading(
      String displayName,
      List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
    return new SummaryCard(
        displayName,
        SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
        probabilityDistributionSummaryViews
    );
  }

  public static SummaryCard probabilitySummaryCard(
      List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {
    return probabilitySummaryCardWithHeading(null, probabilityDistributionSummaryViews);
  }

}
