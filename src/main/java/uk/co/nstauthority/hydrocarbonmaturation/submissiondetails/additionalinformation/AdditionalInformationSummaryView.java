package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.Map;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

public record AdditionalInformationSummaryView(String providingAdditionalComments,
                                               String additionalComments,
                                               String providingAdditionalDocuments,
                                               Map<UploadedFile, String> additionalDocuments) {
  public static AdditionalInformationSummaryView empty() {
    return new AdditionalInformationSummaryView("", "", "", Map.of());
  }
}
