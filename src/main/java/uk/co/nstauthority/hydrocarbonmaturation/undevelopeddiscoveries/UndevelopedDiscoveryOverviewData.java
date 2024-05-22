package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;

public record UndevelopedDiscoveryOverviewData(
    SummaryTableView summaryTableView,
    UndevelopedDiscoveryOverviewStats overviewStats
) {
}
