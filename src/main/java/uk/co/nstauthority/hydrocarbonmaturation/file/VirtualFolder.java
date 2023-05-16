package uk.co.nstauthority.hydrocarbonmaturation.file;

public enum VirtualFolder {
  SUPPORTING_DOCUMENTS("supporting-documents"),
  SHAPEFILE_GEOJSON("shapefile-geojson");

  private final String folderPath;

  VirtualFolder(String folderPath) {
    this.folderPath = folderPath;
  }

  @Override
  public String toString() {
    return folderPath;
  }
}
