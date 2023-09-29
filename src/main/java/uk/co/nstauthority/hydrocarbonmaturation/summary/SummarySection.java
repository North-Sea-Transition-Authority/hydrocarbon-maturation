package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;

public record SummarySection(
    int displayOrder,
    List<SummaryItem> summaryItems,
    String summaryText
) {
}