package uk.co.nstauthority.hydrocarbonmaturation.gis;

enum ShapefileExtension {
  SHP(".shp"),
  SHX(".shx"),
  DBF(".dbf"),
  PRJ(".prj");

  private final String extension;

  ShapefileExtension(String extension) {
    this.extension = extension;
  }

  public String getExtension() {
    return extension;
  }

}
