package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

public enum S3Folder {
  MAPS("maps"),
  IMAGES("images"),
  ;

  private final String folderPath;

  S3Folder(String folderPath) {
    this.folderPath = folderPath;
  }

  @Override
  public String toString() {
    return folderPath;
  }
}
