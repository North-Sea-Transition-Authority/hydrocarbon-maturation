package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

@Service
class ShapefileZipService {
  private final ShapefileImportService shapefileImportService;
  private final SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService;
  private final ShapefileAttributesService shapefileAttributesService;

  public ShapefileZipService(ShapefileImportService shapefileImportService,
                             SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService,
                             ShapefileAttributesService shapefileAttributesService) {
    this.shapefileImportService = shapefileImportService;
    this.supportedCoordinateReferenceSystemService = supportedCoordinateReferenceSystemService;
    this.shapefileAttributesService = shapefileAttributesService;
  }

  public ShapefileZipContents getShapefileZipContents(InputStream shapefileZipInputStream, String filename) {
    ShapefileZipContents shapefileZip;
    try (var zis = new ZipInputStream(shapefileZipInputStream)) {

      shapefileZip = ShapefileZipUtils.getShapefileZipContents(
          zis,
          filename,
          getFileHandler(zis));

    } catch (IOException e) {
      throw new IllegalArgumentException("Could not open shapefile zip %s".formatted(filename), e);
    }
    return shapefileZip;
  }

  private BiConsumer<ShapefileZipContents.Builder, ZipEntry> getFileHandler(ZipInputStream zis) {
    return (shapefileZipBuilder, zipEntry) -> {
      if (zipEntry.getName().endsWith(ShapefileExtension.SHP.getExtension())) {
        handleShapefile(zis, zipEntry, shapefileZipBuilder);
      } else if (zipEntry.getName().endsWith(ShapefileExtension.PRJ.getExtension())) {
        handleShapefileProject(zis, shapefileZipBuilder);
      } else if (zipEntry.getName().endsWith(ShapefileExtension.DBF.getExtension())) {
        handleShapefileDataAttributes(zis, shapefileZipBuilder);
      }
    };
  }

  private void handleShapefile(ZipInputStream zis,
                               ZipEntry zipEntry,
                               ShapefileZipContents.Builder shapefileZipBuilder) {
    try {
      var shapefileStream = new ByteArrayInputStream(zis.readAllBytes());
      var geometries = shapefileImportService.importShapefileAsGeometryList(shapefileStream);
      var filenameWithoutExtension = getFilenameWithoutExtension(zipEntry.getName());
      shapefileZipBuilder.withGeometries(geometries);
      shapefileZipBuilder.withShapefileName(filenameWithoutExtension);
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not import shapefile %s".formatted(zipEntry.getName()), e);
    }
  }

  private void handleShapefileProject(ZipInputStream zis, ShapefileZipContents.Builder shapefileZipBuilder) {
    try {
      var wkt = supportedCoordinateReferenceSystemService.importShapefileProjectAsWkt(zis);
      var thresholds = supportedCoordinateReferenceSystemService.getSpatialReferenceFromWkt(wkt);
      var crs = supportedCoordinateReferenceSystemService.getSupportedCoordinateReferenceSystemFromWkt(wkt);
      shapefileZipBuilder.withThresholds(thresholds);
      shapefileZipBuilder.withCoordinateReferenceSystem(crs);
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not import shapefile project", e);
    }
  }

  private void handleShapefileDataAttributes(ZipInputStream zis,
                                             ShapefileZipContents.Builder shapefileZipBuilder) {

    var shapefileAttributes = shapefileAttributesService.getAttributesFromShapefileDbase(zis);
    shapefileZipBuilder.withAttributes(shapefileAttributes);
  }

  boolean hasRequiredFiles(ShapefileZipContents shapefileZipContents) {
    var filesInZip = shapefileZipContents.filenames();
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

  private String getFilenameWithoutExtension(String fullShapefileName) {
    return FilenameUtils.getBaseName(fullShapefileName);
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
}
