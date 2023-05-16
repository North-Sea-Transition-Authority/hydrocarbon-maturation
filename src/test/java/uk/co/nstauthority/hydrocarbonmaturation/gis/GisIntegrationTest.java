package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;

@IntegrationTest
class GisIntegrationTest {

  @Autowired
  private ShapefileImportService shapefileImportService;
  @Autowired
  private GeoJsonExportService geoJsonExportService;
  @Autowired
  private ShapefileZipService shapefileZipService;
  @Autowired
  private SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService;
  @Autowired
  private ShapefileAttributesService shapefileAttributesService;

  @Test
  void convertShapeFileAndConvertToGeoJson_ResultShouldMatchPreConvertedGeoJson() throws IOException {
    var spatialReference = importShapefileProjectAsSpatialReference();
    var geometries = importShapefileAsGeometryList();
    var supportedCoordinateReferenceSystem = SupportedCoordinateReferenceSystem.WGS84;
    var result = geoJsonExportService.exportGeometryAsGeoJson(spatialReference, supportedCoordinateReferenceSystem, geometries);

    var geoJson = ShapefileTestUtils.getStreamAsString(ShapefileTestUtils.GEOJSON_RESOURCE_LOCATION);

    assertThat(result).isEqualTo(geoJson);
  }

  @Test
  void importEmptyShapefile_ShouldReturnEmptyGeometryList() throws IOException {
    var geometries = ShapefileTestUtils.getStreamAsShapefileGeometryLoader(ShapefileTestUtils.EMPTY_SHAPEFILE_RESOURCE_LOCATION);

    assertThat(geometries.getShapefileHeader().geometryType()).isEqualTo(Geometry.Type.Point);
    assertThat(geometries.getGeometryList()).isEmpty();
  }

  @Test
  void importCorruptShapefile_ThrowsException() {
    assertThrows(EOFException.class, () ->
        ShapefileTestUtils.getStreamAsShapefileGeometryLoader(ShapefileTestUtils.CORRUPT_SHAPEFILE_RESOURCE_LOCATION)
    );
  }

  @Test
  void zipHasFilesAndCanOpenGeometry() throws IOException {
    var shapefileZipContents = getShapefileZipContents(ShapefileTestUtils.SHAPEFILE_ZIP_RESOURCE_LOCATION);
    assertThat(shapefileZipContents.geometries()).isNotEmpty();
  }

  @Test
  void testShapefileDbase() throws IOException {
    var result = importShapefileDbase();

    assertThat(result.headers())
        .containsExactly("gid", "id", "objectid", "polygon_id", "resource_i", "pid", "circle", "shape_leng", "shape_area");

    assertThat(result.rows())
        .containsExactly(
            List.of("1", "2", "1", "cp53_5_2", "RID0007075", "PID0007075", "0", "0.239981417960000", "0.001359597960000")
        );
  }



  private ShapefileZipContents getShapefileZipContents(String shapefileZipResourceLocation) throws IOException {
    try (var shapefileZipStream = ShapefileTestUtils.getFileInputStream(shapefileZipResourceLocation)) {
      return shapefileZipService.getShapefileZipContents(shapefileZipStream, "test");
    }
  }

  private List<Geometry> importShapefileAsGeometryList() throws IOException {
    try (var shapefileStream = ShapefileTestUtils.getFileInputStream(ShapefileTestUtils.SHAPEFILE_RESOURCE_LOCATION)) {
      return shapefileImportService.importShapefileAsGeometryList(shapefileStream);
    }
  }

  private SpatialReference importShapefileProjectAsSpatialReference() throws IOException {
    try (var shapefileProjectStream = ShapefileTestUtils.getFileInputStream(ShapefileTestUtils.SHAPEFILE_PROJECT_RESOURCE_LOCATION)) {
      var wkt = supportedCoordinateReferenceSystemService.importShapefileProjectAsWkt(shapefileProjectStream);
      return supportedCoordinateReferenceSystemService.getSpatialReferenceFromWkt(wkt);
    }
  }

  private ShapefileAttributes importShapefileDbase() throws IOException {
    try (var shapefileProjectStream = ShapefileTestUtils.getFileInputStream(ShapefileTestUtils.SHAPEFILE_DBASE_RESOURCE_LOCATION)) {
      return shapefileAttributesService.getAttributesFromShapefileDbase(shapefileProjectStream);
    }
  }
}