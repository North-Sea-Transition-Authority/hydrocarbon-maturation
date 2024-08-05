package uk.co.nstauthority.hydrocarbonmaturation.summary;

import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

/**
 * This class holds the file data for downloading from the summary.
 */
public record SummaryFileView(
    String filename,
    String description,
    String downloadUrl
) {

  public static SummaryFileView from(UploadedFile uploadedFile, String downloadUrl) {
    return new SummaryFileView(
        uploadedFile.getName(),
        uploadedFile.getDescription(),
        downloadUrl
    );
  }

}
