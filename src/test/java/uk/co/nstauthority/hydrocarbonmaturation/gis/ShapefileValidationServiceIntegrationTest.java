package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@IntegrationTest
class ShapefileValidationServiceIntegrationTest {

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
  void isGeometryValid_SimplePolygonInZip_True() throws IOException {
    var shapefileZipContents = getShapefileZipContents(ShapefileTestUtils.SHAPEFILE_ZIP_RESOURCE_LOCATION);

    var result = shapefileValidationService.isGeometryValid(shapefileZipContents.thresholds(), shapefileZipContents.geometries().get(0));

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
  void validateShapefileZip_ValidateShapefileProjectOffshore() throws IOException {
    var result = validateShapefileProjectInZip(ShapefileTestUtils.SHAPEFILE_ZIP_RESOURCE_LOCATION, OnshoreOffshore.OFFSHORE);

    assertThat(result).isTrue();
  }

  @Test
  void validateShapefileZip_ValidateShapefileProjectOnshore() throws IOException {
    var result = validateShapefileProjectInZip(ShapefileTestUtils.SHAPEFILE_ZIP_ONSHORE_RESOURCE_LOCATION, OnshoreOffshore.ONSHORE);

    assertThat(result).isTrue();
  }

  @Test
  void validateWktHasSupportedCoordinateReferenceSystem_HasSupportedCrs() throws IOException {
    var supportedCoordinateReferenceSystem = getSupportedCoordinateReferenceSystem(ShapefileTestUtils.SHAPEFILE_PROJECT_RESOURCE_LOCATION);
    var shapefileZipContents = ShapefileZipContents.builder().withCoordinateReferenceSystem(supportedCoordinateReferenceSystem).build();

    var result = shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(shapefileZipContents);

    assertThat(result).isTrue();
  }

  @Test
  void validateWktHasSupportedCoordinateReferenceSystem_ProjectionDefinition_HasSupportedCrs() throws IOException {
    var supportedCoordinateReferenceSystem = getSupportedCoordinateReferenceSystem(ShapefileTestUtils.PROJCS_SHAPEFILE_PROJECT_RESOURCE_LOCATION);
    var shapefileZipContents = ShapefileZipContents.builder().withCoordinateReferenceSystem(supportedCoordinateReferenceSystem).build();

    var result = shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(shapefileZipContents);

    assertThat(result).isTrue();
  }

  @Test
  void validateWktHasSupportedCoordinateReferenceSystem_DoesNotHaveSupportedCrs() throws IOException {
    var supportedCoordinateReferenceSystem = getSupportedCoordinateReferenceSystem(ShapefileTestUtils.UNSUPPORTED_SHAPEFILE_PROJECT_RESOURCE_LOCATION);
    var shapefileZipContents = ShapefileZipContents.builder().withCoordinateReferenceSystem(supportedCoordinateReferenceSystem).build();

    var result = shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(shapefileZipContents);

    assertThat(result).isFalse();
  }

  private Geometry getGeometryFromShapefile(String shapefileResourceLocation) throws IOException {
    try (var shapefileStream = ShapefileTestUtils.getFileInputStream(shapefileResourceLocation)) {
      var geometryList = shapefileImportService.importShapefileAsGeometryList(shapefileStream);
      return geometryList.get(0);
    }
  }

  private SpatialReference getSpatialReferenceFromShapefileProject(String shapefileProjectResourceLocation) throws IOException {
    try (var inputStream = ShapefileTestUtils.getFileInputStream(shapefileProjectResourceLocation)) {
      var wkt = supportedCoordinateReferenceSystemService.importShapefileProjectAsWkt(inputStream);
      return supportedCoordinateReferenceSystemService.getSpatialReferenceFromWkt(wkt);
    }
  }

  private boolean validateShapefileZip(String shapefileZipResourceLocation) throws IOException {
    var shapefileZipContents = getShapefileZipContents(shapefileZipResourceLocation);
    return shapefileZipService.hasRequiredFiles(shapefileZipContents);
  }

  private boolean validateShapefileProjectInZip(String shapefileZipResourceLocation, OnshoreOffshore onshoreOffshore) throws IOException {
    var shapefileZip = getShapefileZipContents(shapefileZipResourceLocation);
    return shapefileValidationService.supportedCoordinateReferenceSystemByOnshoreOrOffshoreIsValid(shapefileZip,
        onshoreOffshore);
  }

  private ShapefileZipContents getShapefileZipContents(String shapefileZipResourceLocation) throws IOException {
    try (var shapefileZipStream = ShapefileTestUtils.getFileInputStream(shapefileZipResourceLocation)) {
      return shapefileZipService.getShapefileZipContents(shapefileZipStream, "test");
    }
  }

  private SupportedCoordinateReferenceSystem getSupportedCoordinateReferenceSystem(String shapefileProjectResourceLocation) throws IOException {
    try (var inputStream = ShapefileTestUtils.getFileInputStream(shapefileProjectResourceLocation)) {
      var wkt = supportedCoordinateReferenceSystemService.importShapefileProjectAsWkt(inputStream);
      return supportedCoordinateReferenceSystemService.getSupportedCoordinateReferenceSystemFromWkt(wkt);
    }
  }
}