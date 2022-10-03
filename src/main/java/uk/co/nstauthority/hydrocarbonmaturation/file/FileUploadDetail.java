package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.time.Instant;

public class FileUploadDetail {
  private String uploadedFileId;

  private String uploadedFileDescription;

  private Instant uploadedFileInstant;

  public String getUploadedFileId() {
    return uploadedFileId;
  }

  public void setUploadedFileId(String uploadedFileId) {
    this.uploadedFileId = uploadedFileId;
  }

  public String getUploadedFileDescription() {
    return uploadedFileDescription;
  }

  public void setUploadedFileDescription(String uploadedFileDescription) {
    this.uploadedFileDescription = uploadedFileDescription;
  }

  public Instant getUploadedFileInstant() {
    return uploadedFileInstant;
  }

  public void setUploadedFileInstant(Instant uploadedFileInstant) {
    this.uploadedFileInstant = uploadedFileInstant;
  }
}
