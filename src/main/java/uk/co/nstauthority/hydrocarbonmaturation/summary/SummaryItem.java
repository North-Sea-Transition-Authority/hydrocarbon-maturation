package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;

public record SummaryItem(
    String displayName,
    List<SummaryCard> summaryCards,
    List<SummaryAction> summarySectionActions
) {
  public SummaryItem(String displayName, List<SummaryCard> summaryCards) {
    this(displayName, summaryCards, List.of());
  }

  public static SummaryItem withCard(String displayName,
                                     SummaryCard summaryCard) {
    return SummaryItem.withCardAndActions(displayName, summaryCard, List.of());
  }

  public static SummaryItem withCardAndActions(String displayName,
                                               SummaryCard summaryCard,
                                               List<SummaryAction> summarySectionActions) {
    return new SummaryItem(
        displayName,
        summaryCard != null
            ? List.of(summaryCard)
            : SummaryCard.emptySummaryCardList(),
        summarySectionActions
    );
  }

  public static SummaryItem withCards(String displayName,
                                      List<SummaryCard> summaryCards) {
    return SummaryItem.withCardsAndActions(displayName, summaryCards, List.of());
  }

  public static SummaryItem withCardsAndActions(String displayName,
                                                List<SummaryCard> summaryCards,
                                                List<SummaryAction> summarySectionActions) {
    return new SummaryItem(
        displayName,
        summaryCards != null && !summaryCards.isEmpty()
            ? summaryCards
            : SummaryCard.emptySummaryCardList(),
        summarySectionActions
    );
  }
}
