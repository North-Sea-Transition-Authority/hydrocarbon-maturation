package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;

@IntegrationTest
class ShapefileValidationServiceTest {

  public static final String CLASSPATH_SHAPEFILES = "classpath:shapefiles";
  private static final String SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.shp";
  private static final String NOT_SIMPLE_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/not_simple.shp";
  private static final String NOT_SIMPLE_OGC_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/not_simple_ogc.shp";
  private static final String SHAPEFILE_PROJECT_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.prj";
  private static final String SHAPEFILE_ZIP_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.zip";
  private static final String INVALID_SHAPEFILE_ZIP_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/missing.zip";

  @Autowired
  private ShapefileImportService shapefileImportService;
  @Autowired
  private ShapefileValidationService shapefileValidationService;
  @Autowired
  private ShapefileZipService shapefileZipService;

  @Test
  void isGeometryValid_SimplePolygon_True() throws IOException {
    var geometry = getGeometryFromShapefile(SHAPEFILE_RESOURCE_LOCATION);
    var spatialReference = getSpatialReferenceFromShapefileProject();

    var result = shapefileValidationService.isGeometryValid(spatialReference, geometry);

    assertThat(result).isTrue();
  }

  @Test
  void isGeometryValid_NonSimple_False() throws IOException {
    var geometry = getGeometryFromShapefile(NOT_SIMPLE_SHAPEFILE_RESOURCE_LOCATION);

    assertThat(shapefileValidationService.isGeometryValid(null, geometry)).isFalse();
  }

  @Test
  void isGeometryValidStrict_SimplePolygon_True() throws IOException {
    var geometry = getGeometryFromShapefile(SHAPEFILE_RESOURCE_LOCATION);
    var spatialReference = getSpatialReferenceFromShapefileProject();

    var result = shapefileValidationService.isGeometryValidStrict(spatialReference, geometry);

    assertThat(result).isTrue();
  }

  @Test
  void isGeometryValidStrict_NonSimpleOgc_False() throws IOException {
    var geometry = getGeometryFromShapefile(NOT_SIMPLE_OGC_SHAPEFILE_RESOURCE_LOCATION);

    assertThat(shapefileValidationService.isGeometryValidStrict(null, geometry)).isFalse();
  }

  @Test
  void validateGeometryIsOfType() throws IOException {
    var geometry = getGeometryFromShapefile(SHAPEFILE_RESOURCE_LOCATION);

    var result = shapefileValidationService.validateGeometryIsOfType(geometry, Geometry.Type.Polygon);

    assertThat(result).isTrue();
  }

  @Test
  void validateShapefileZip_ContainsAllFiles() throws IOException {
    var result = validateShapefileZip(SHAPEFILE_ZIP_RESOURCE_LOCATION);

    assertThat(result).isTrue();
  }

  @Test
  void validateShapefileZip_HasMissingFiles() throws IOException {
    var result = validateShapefileZip(INVALID_SHAPEFILE_ZIP_RESOURCE_LOCATION);

    assertThat(result).isFalse();
  }

  private Geometry getGeometryFromShapefile(String shapefileResourceLocation) throws IOException {
    try (var shapefileStream = new FileInputStream(ResourceUtils.getFile(shapefileResourceLocation))) {
      var geometryList = shapefileImportService.importShapefileAsGeometryList(shapefileStream);
      return geometryList.get(0);
    }
  }

  private SpatialReference getSpatialReferenceFromShapefileProject() throws IOException {
    try (var shapefileStream = new FileInputStream(ResourceUtils.getFile(SHAPEFILE_PROJECT_RESOURCE_LOCATION))) {
      return shapefileImportService.importShapefileProjectAsSpatialReference(shapefileStream);
    }
  }

  private boolean validateShapefileZip(String shapefileZipResourceLocation) throws IOException {
    try (var shapefileZipStream = new FileInputStream(ResourceUtils.getFile(shapefileZipResourceLocation))) {
      try (var zis = new ZipInputStream(shapefileZipStream)) {
        var filesInZip = ZipUtils.getFilesInZip(zis);
        return shapefileZipService.hasRequiredFiles(filesInZip);
      }
    }
  }
}