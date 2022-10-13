package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.time.Instant;

public record FileUploadFdsView(String fileId, String fileName, String fileSize, String fileDescription,
                                Instant fileUploadedTime) {
  // we need these getters as FDS' file-upload macro expects the getters to be named in this specific way
  public String getFileId() {
    return fileId;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFileSize() {
    return fileSize;
  }

  public String getFileDescription() {
    return fileDescription;
  }

  public Instant getFileUploadedTime() {
    return fileUploadedTime;
  }
}
