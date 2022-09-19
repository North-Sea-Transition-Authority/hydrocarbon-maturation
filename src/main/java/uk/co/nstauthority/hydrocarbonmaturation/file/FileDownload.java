package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.io.InputStream;

public class FileDownload {
  private final UploadedFile uploadedFile;
  private final InputStream inputStream;

  public FileDownload(UploadedFile uploadedFile, InputStream inputStream) {

    this.uploadedFile = uploadedFile;
    this.inputStream = inputStream;
  }

  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  public InputStream getInputStream() {
    return inputStream;
  }
}
