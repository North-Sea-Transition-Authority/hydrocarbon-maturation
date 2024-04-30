package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;

public record SummaryCrossTabView(
    List<String> headings,
    List<? extends CrossTabRow> rows
) {
}