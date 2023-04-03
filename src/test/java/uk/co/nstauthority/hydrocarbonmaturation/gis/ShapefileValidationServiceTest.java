package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;

@IntegrationTest
class ShapefileValidationServiceTest {

  private static final String SHAPEFILE_RESOURCE_LOCATION = "classpath:shapefiles/test.shp";
  private static final String SHAPEFILE_PROJECT_RESOURCE_LOCATION = "classpath:shapefiles/test.prj";

  @Autowired
  private ShapefileImportService shapefileImportService;
  @Autowired
  private ShapefileValidationService shapefileValidationService;

  @Test
  void isGeometryValid() throws IOException {
    var geometry = getGeometryFromShapefile();
    var spatialReference = getSpatialReferenceFromShapefileProject();

    var result = shapefileValidationService.isGeometryValid(spatialReference, geometry);

    assertThat(result).isTrue();
  }

  @Test
  void isGeometryValidStrict() throws IOException {
    var geometry = getGeometryFromShapefile();
    var spatialReference = getSpatialReferenceFromShapefileProject();

    var result = shapefileValidationService.isGeometryValidStrict(spatialReference, geometry);

    assertThat(result).isTrue();
  }

  @Test
  void validateGeometryIsOfType() throws IOException {
    var geometry = getGeometryFromShapefile();

    var result = shapefileValidationService.validateGeometryIsOfType(geometry, Geometry.Type.Polygon);

    assertThat(result).isTrue();
  }

  private Geometry getGeometryFromShapefile() throws IOException {
    try (var shapefileStream = new FileInputStream(ResourceUtils.getFile(SHAPEFILE_RESOURCE_LOCATION))) {
      var geometryList = shapefileImportService.importShapefileAsGeometryList(shapefileStream);
      return geometryList.get(0);
    }
  }

  private SpatialReference getSpatialReferenceFromShapefileProject() throws IOException {
    try (var shapefileStream = new FileInputStream(ResourceUtils.getFile(SHAPEFILE_PROJECT_RESOURCE_LOCATION))) {
      return shapefileImportService.importShapefileProjectAsSpatialReference(shapefileStream);
    }
  }
}