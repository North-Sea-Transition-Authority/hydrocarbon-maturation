package uk.co.nstauthority.hydrocarbonmaturation.summary;

import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

public record SummaryFileViewRow(String key, SummaryFileView fileView) {
  public static SummaryFileViewRow from(String heading, UploadedFile uploadedFile, String downloadUrl) {
    return new SummaryFileViewRow(
        heading,
        SummaryFileView.from(uploadedFile, downloadUrl)
    );
  }

  public static SummaryFileViewRow empty(String heading) {
    return new SummaryFileViewRow(heading, new SummaryFileView(null, null, null));
  }
}
