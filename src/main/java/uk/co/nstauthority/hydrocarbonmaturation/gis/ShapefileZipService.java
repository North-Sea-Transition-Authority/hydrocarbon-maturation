package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;
import javax.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
class ShapefileZipService {
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

  @Nullable
  String getShapefileName(List<String> filesInZip) {
    var shapefilesInZip = getShapefilesInZip(filesInZip);

    var hasOneShapefile = shapefilesInZip.size() == 1;
    if (hasOneShapefile) {
      return getFilenameWithoutExtension(filesInZip.get(0));
    }
    return null;
  }

  boolean hasRequiredFiles(List<String> filesInZip) {
    var shapefilesInZip = getShapefilesInZip(filesInZip);

    var hasOneShapefile = shapefilesInZip.size() == 1;
    if (!hasOneShapefile) {
      return false;
    }

    var shapefileName = getFilenameWithoutExtension(filesInZip.get(0));

    var hasShxFile = zipHasFile(filesInZip, shapefileName + ShapefileExtension.SHX.getExtension());
    var hasPrjFile = zipHasFile(filesInZip, shapefileName + ShapefileExtension.PRJ.getExtension());
    var hasDbfFile = zipHasFile(filesInZip, shapefileName + ShapefileExtension.DBF.getExtension());

    return hasShxFile && hasPrjFile && hasDbfFile;
  }

  InputStream getInputStreamForShapefile(ZipInputStream inputStream, String shapefileName) throws IOException {

    return ZipUtils.getInputStreamForFileInZip(inputStream, shapefileName + ShapefileExtension.SHP.getExtension());
  }

  InputStream getInputStreamForShapefileProjection(ZipInputStream inputStream, String shapefileName) throws IOException {

    return ZipUtils.getInputStreamForFileInZip(inputStream, shapefileName + ShapefileExtension.PRJ.getExtension());
  }

  private static List<String> getShapefilesInZip(List<String> filesInZip) {
    return filesInZip.stream()
        .filter(file -> file.endsWith(ShapefileExtension.SHP.getExtension()))
        .toList();
  }

  private boolean zipHasFile(List<String> filesInZip, String filename) {
    return filesInZip.stream()
        .anyMatch(file -> file.equalsIgnoreCase(filename));
  }

  private static String getFilenameWithoutExtension(String fullShapefileName) {
    return fullShapefileName.substring(0, fullShapefileName.length() - 4);
  }

}
