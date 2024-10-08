package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService.FILE_ROW_KEY;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

public record RegulatorManagedInformationSummaryView(String responsibleTeamType,
                                                     String geographicalArea,
                                                     boolean isBeingRelinquished,
                                                     List<SummaryFileViewRow> relinquishmentReport,
                                                     String linkedToFieldName,
                                                     String linkedToFieldUrl) {
  public static RegulatorManagedInformationSummaryView empty(boolean isBeingRelinquished) {
    return new RegulatorManagedInformationSummaryView(
        "",
        "",
        isBeingRelinquished,
        List.of(SummaryFileViewRow.empty(FILE_ROW_KEY)),
        "",
        ""
    );
  }
}
