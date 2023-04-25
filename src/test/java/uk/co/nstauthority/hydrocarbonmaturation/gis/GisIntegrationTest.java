package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.gis.esri.ShapefileGeometryLoader;

@IntegrationTest
class GisIntegrationTest {

  private static final String CLASSPATH_SHAPEFILES = "classpath:shapefiles";
  private static final String SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.shp";
  private static final String SHAPEFILE_PROJECT_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.prj";
  private static final String EMPTY_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/empty.shp";
  private static final String CORRUPT_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/corrupt.shp";
  private static final String GEOJSON_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.json";
  private static final String SHAPEFILE_ZIP_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.zip";

  @Autowired
  private ShapefileImportService shapefileImportService;
  @Autowired
  private GeoJsonExportService geoJsonExportService;
  @Autowired
  private ShapefileZipService shapefileZipService;

  @Test
  void convertShapeFileAndConvertToGeoJson_ResultShouldMatchPreConvertedGeoJson() throws IOException {
    var spatialReference = importShapefileProjectAsSpatialReference();
    var geometries = importShapefileAsGeometryList();
    var result = geoJsonExportService.exportShapefileAsGeoJson(spatialReference, geometries);

    var geoJson = getPreConvertedGeoJson();

    assertThat(result).isEqualTo(geoJson);
  }

  @Test
  void importEmptyShapefile_ShouldReturnEmptyGeometryList() throws IOException {
    var geometries = importShapefile(EMPTY_SHAPEFILE_RESOURCE_LOCATION);

    assertThat(geometries.getShapefileHeader().geometryType()).isEqualTo(Geometry.Type.Point);
    assertThat(geometries.getGeometryList()).isEmpty();
  }

  @Test
  void importCorruptShapefile_ThrowsException() {
    assertThrows(EOFException.class, () ->
        importShapefile(CORRUPT_SHAPEFILE_RESOURCE_LOCATION)
    );
  }

  @Test
  void zipHasFilesAndCanOpenGeometry() throws IOException {
    var geometries = importShapefileZip(SHAPEFILE_ZIP_RESOURCE_LOCATION);

    assertThat(geometries.getShapefileHeader().geometryType()).isEqualTo(Geometry.Type.Polygon);
    assertThat(geometries.getGeometryList()).isNotEmpty();
  }

  private ShapefileGeometryLoader importShapefileZip(String shapefileZipResourceLocation) throws IOException {
    try (var shapefileZipStream = new FileInputStream(ResourceUtils.getFile(shapefileZipResourceLocation))) {
      try (var zis = new ZipInputStream(shapefileZipStream)) {
        var inputStream = shapefileZipService.getInputStreamForShapefile(zis, "test");

        return new ShapefileGeometryLoader(inputStream);
      }
    }
  }

  private static String getPreConvertedGeoJson() throws IOException {
    try (var jsonStream = new FileInputStream(ResourceUtils.getFile(GEOJSON_RESOURCE_LOCATION))) {
      return new String(jsonStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private List<Geometry> importShapefileAsGeometryList() throws IOException {
    try (var shapefileStream = new FileInputStream(ResourceUtils.getFile(GisIntegrationTest.SHAPEFILE_RESOURCE_LOCATION))) {
      return shapefileImportService.importShapefileAsGeometryList(shapefileStream);
    }
  }

  private ShapefileGeometryLoader importShapefile(String shapefileResourceLocation) throws IOException {
    try (var shapefileStream = new FileInputStream(ResourceUtils.getFile(shapefileResourceLocation))) {
      return new ShapefileGeometryLoader(shapefileStream);
    }
  }

  private SpatialReference importShapefileProjectAsSpatialReference() throws IOException {
    try (var shapefileProjectStream = new FileInputStream(ResourceUtils.getFile(SHAPEFILE_PROJECT_RESOURCE_LOCATION))) {
      return shapefileImportService.importShapefileProjectAsSpatialReference(shapefileProjectStream);
    }
  }
}