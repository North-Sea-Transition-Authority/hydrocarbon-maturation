package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;

@IntegrationTest
class ShapefileValidationServiceTest {

  @Autowired
  private ShapefileImportService shapefileImportService;
  @Autowired
  private ShapefileValidationService shapefileValidationService;
  @Autowired
  private ShapefileZipService shapefileZipService;
  @Autowired
  private SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService;

  @Test
  void isGeometryValid_SimplePolygon_True() throws IOException {
    var geometry = getGeometryFromShapefile(ShapefileTestUtils.SHAPEFILE_RESOURCE_LOCATION);
    var spatialReference = getSpatialReferenceFromShapefileProject(ShapefileTestUtils.SHAPEFILE_PROJECT_RESOURCE_LOCATION);

    var result = shapefileValidationService.isGeometryValid(spatialReference, geometry);

    assertThat(result).isTrue();
  }

  @Test
  void isGeometryValid_NonSimple_False() throws IOException {
    var geometry = getGeometryFromShapefile(ShapefileTestUtils.NOT_SIMPLE_SHAPEFILE_RESOURCE_LOCATION);

    assertThat(shapefileValidationService.isGeometryValid(null, geometry)).isFalse();
  }

  @Test
  void isGeometryValidStrict_SimplePolygon_True() throws IOException {
    var geometry = getGeometryFromShapefile(ShapefileTestUtils.SHAPEFILE_RESOURCE_LOCATION);
    var spatialReference = getSpatialReferenceFromShapefileProject(ShapefileTestUtils.SHAPEFILE_PROJECT_RESOURCE_LOCATION);

    var result = shapefileValidationService.isGeometryValidStrict(spatialReference, geometry);

    assertThat(result).isTrue();
  }

  @Test
  void isGeometryValidStrict_NonSimpleOgc_False() throws IOException {
    var geometry = getGeometryFromShapefile(ShapefileTestUtils.NOT_SIMPLE_OGC_SHAPEFILE_RESOURCE_LOCATION);

    assertThat(shapefileValidationService.isGeometryValidStrict(null, geometry)).isFalse();
  }

  @Test
  void validateGeometryIsOfType() throws IOException {
    var geometry = getGeometryFromShapefile(ShapefileTestUtils.SHAPEFILE_RESOURCE_LOCATION);

    var result = shapefileValidationService.validateGeometryIsOfType(geometry, Geometry.Type.Polygon);

    assertThat(result).isTrue();
  }

  @Test
  void validateShapefileZip_ContainsAllFiles() throws IOException {
    var result = validateShapefileZip(ShapefileTestUtils.SHAPEFILE_ZIP_RESOURCE_LOCATION);

    assertThat(result).isTrue();
  }

  @Test
  void validateShapefileZip_HasMissingFiles() throws IOException {
    var result = validateShapefileZip(ShapefileTestUtils.INVALID_SHAPEFILE_ZIP_RESOURCE_LOCATION);

    assertThat(result).isFalse();
  }

  @Test
  void validateShapefileZip_ValidateShapefileProject() throws IOException {

    var result = validateShapefileProjectInZip(ShapefileTestUtils.SHAPEFILE_ZIP_RESOURCE_LOCATION);

    assertThat(result).isTrue();
  }

  @Test
  void validateWktHasSupportedCoordinateReferenceSystem_HasSupportedCrs() throws IOException {
    var wkt = getWktFromShapefileProject(ShapefileTestUtils.SHAPEFILE_PROJECT_RESOURCE_LOCATION);

    var result = shapefileValidationService.validateWktHasSupportedCoordinateReferenceSystem(wkt);

    assertThat(result).isTrue();
  }

  @Test
  void validateWktHasSupportedCoordinateReferenceSystem_ProjectionDefinition_HasSupportedCrs() throws IOException {
    var wkt = getWktFromShapefileProject(ShapefileTestUtils.PROJCS_SHAPEFILE_PROJECT_RESOURCE_LOCATION);

    var result = shapefileValidationService.validateWktHasSupportedCoordinateReferenceSystem(wkt);

    assertThat(result).isTrue();
  }

  @Test
  void validateWktHasSupportedCoordinateReferenceSystem_DoesNotHaveSupportedCrs() throws IOException {
    var wkt = getWktFromShapefileProject(ShapefileTestUtils.UNSUPPORTED_SHAPEFILE_PROJECT_RESOURCE_LOCATION);

    var result = shapefileValidationService.validateWktHasSupportedCoordinateReferenceSystem(wkt);

    assertThat(result).isFalse();
  }

  private Geometry getGeometryFromShapefile(String shapefileResourceLocation) throws IOException {
    try (var shapefileStream = ShapefileTestUtils.getFileInputStream(shapefileResourceLocation)) {
      var geometryList = shapefileImportService.importShapefileAsGeometryList(shapefileStream);
      return geometryList.get(0);
    }
  }

  private SpatialReference getSpatialReferenceFromShapefileProject(String shapefileProjectResourceLocation) throws IOException {
    return supportedCoordinateReferenceSystemService
        .getSpatialReferenceFromWkt(getWktFromShapefileProject(shapefileProjectResourceLocation));
  }

  private String getWktFromShapefileProject(String shapefileProjectResourceLocation) throws IOException {
    try (var shapefileStream = ShapefileTestUtils.getFileInputStream(shapefileProjectResourceLocation)) {
      return shapefileImportService.importShapefileProjectAsWkt(shapefileStream);
    }
  }

  private boolean validateShapefileZip(String shapefileZipResourceLocation) throws IOException {
    try (var shapefileZipStream = ShapefileTestUtils.getFileInputStream(shapefileZipResourceLocation)) {
      try (var zis = new ZipInputStream(shapefileZipStream)) {
        var filesInZip = ZipUtils.getFilesInZip(zis);
        return shapefileZipService.hasRequiredFiles(filesInZip);
      }
    }
  }

  private boolean validateShapefileProjectInZip(String shapefileZipResourceLocation) throws IOException {
    try (var shapefileZipStream = ShapefileTestUtils.getFileInputStream(shapefileZipResourceLocation)) {
      try (var zis = new ZipInputStream(shapefileZipStream)) {
        var inputStream = shapefileZipService.getInputStreamForShapefileProjection(zis, "test");
        var wkt = shapefileImportService.importShapefileProjectAsWkt(inputStream);
        return shapefileValidationService.validateWktHasSupportedCoordinateReferenceSystem(wkt);
      }
    }
  }
}