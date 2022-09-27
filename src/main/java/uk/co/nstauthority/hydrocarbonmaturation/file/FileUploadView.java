package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.time.Instant;

public final class FileUploadView {
  private final String fileId;
  private final String fileName;
  private final String fileSize;
  private final String fileDescription;
  private final Instant fileUploadedTime;

  public FileUploadView(String fileId, String fileName, String fileSize, String fileDescription, Instant fileUploadedTime) {
    this.fileId = fileId;
    this.fileName = fileName;
    this.fileSize = fileSize;
    this.fileDescription = fileDescription;
    this.fileUploadedTime = fileUploadedTime;
  }

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
