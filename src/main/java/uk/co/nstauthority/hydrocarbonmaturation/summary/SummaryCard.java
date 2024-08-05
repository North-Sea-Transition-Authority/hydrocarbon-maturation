package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;

public record SummaryCard(
    String displayName,
    SummaryCardType summaryCardType,
    Object summaryData,
    List<SummaryAction> summaryActions,
    List<SummaryFileViewRow> fileViews
) {

  public SummaryCard(String displayName, SummaryCardType summaryCardType, Object summaryData) {
    this(displayName, summaryCardType, summaryData, List.of(), List.of());
  }

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

  public static SummaryCard probabilitySummaryCardWithHeading(String displayName,
                                                              ProbabilityDistributionTableView tableView) {
    return new SummaryCard(
        displayName,
        SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
        tableView
    );
  }

  public static SummaryCard probabilitySummaryCard(ProbabilityDistributionTableView tableView) {
    return probabilitySummaryCardWithHeading(null, tableView);
  }


  public static SummaryCard tableFixedWidthSummaryCardWithHeading(String displayName,
                                                                  SummaryTableView summaryData) {
    return new SummaryCard(
        displayName,
        SummaryCardType.TABLE_FIXED_WIDTH_SUMMARY,
        summaryData
    );
  }

  public static SummaryCard simpleSummaryWithFiles(String heading, SummaryDataView summaryData,
                                                   List<SummaryFileViewRow> fileViews) {
    return new SummaryCard(
        heading,
        SummaryCardType.SIMPLE_SUMMARY_WITH_FILES,
        summaryData,
        List.of(),
        fileViews
    );
  }

  public static SummaryCard emptySummaryCardWithActions(String heading, List<SummaryAction> actions) {
    return new SummaryCard(
        heading,
        SummaryCardType.EMPTY_SUMMARY,
        null,
        actions,
        List.of()
    );
  }

  public static SummaryCard crossTabSummaryCard(String heading, SummaryCrossTabView crossTabView) {
    return new SummaryCard(
        heading,
        SummaryCardType.CROSS_TAB,
        crossTabView,
        List.of(),
        List.of()
    );
  }
}
