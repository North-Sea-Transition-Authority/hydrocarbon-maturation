package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryService.FILE_ROW_KEY;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

public record OutlineForMaximumCaseSummaryView(List<SummaryFileViewRow> outlineForMaximumCaseShapefile,
                                               boolean hasVerifiedShapefile,
                                               String mapGeoJsonDataRoute,
                                               String mapAttributions) {
  public static OutlineForMaximumCaseSummaryView empty() {
    return new OutlineForMaximumCaseSummaryView(
        List.of(SummaryFileViewRow.empty(FILE_ROW_KEY)),
        false,
        "",
        ""
    );
  }
}