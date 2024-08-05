package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSummaryService.FILE_ROW_KEY;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

public record AdditionalInformationSummaryView(String providingAdditionalComments,
                                               String additionalComments,
                                               String providingAdditionalDocuments,
                                               List<SummaryFileViewRow> additionalDocuments) {
  public static AdditionalInformationSummaryView empty() {
    return new AdditionalInformationSummaryView("", "", "", List.of(SummaryFileViewRow.empty(FILE_ROW_KEY)));
  }
}
