package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;

@IntegrationTest
class GisIntegrationTest {

  private static final String SHAPEFILE_RESOURCE_LOCATION = "classpath:shapefiles/test.shp";
  private static final String GEOJSON_RESOURCE_LOCATION = "classpath:shapefiles/test.json";
  private static final String SHAPEFILE_PROJECT_RESOURCE_LOCATION = "classpath:shapefiles/test.prj";

  @Autowired
  private ShapefileImportService shapefileImportService;
  @Autowired
  private GeoJsonExportService geoJsonExportService;

  @Test
  void convertShapeFileAndConvertToGeoJson_ResultShouldMatchPreConvertedGeoJson() throws IOException {
    var spatialReference = importShapefileProjectAsSpatialReference();
    var geometries = importShapefileAsGeometryList();
    var result = geoJsonExportService.exportShapefileAsGeoJson(spatialReference, geometries);

    var geoJson = getPreConvertedGeoJson();

    assertThat(result).isEqualTo(geoJson);
  }

  private static String getPreConvertedGeoJson() throws IOException {
    try (var jsonStream = new FileInputStream(ResourceUtils.getFile(GEOJSON_RESOURCE_LOCATION))) {
      return new String(jsonStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private List<Geometry> importShapefileAsGeometryList() throws IOException {
    try (var shapefileStream = new FileInputStream(ResourceUtils.getFile(SHAPEFILE_RESOURCE_LOCATION))) {
      return shapefileImportService.importShapefileAsGeometryList(shapefileStream);
    }
  }

  private SpatialReference importShapefileProjectAsSpatialReference() throws IOException {
    try (var shapefileProjectStream = new FileInputStream(ResourceUtils.getFile(SHAPEFILE_PROJECT_RESOURCE_LOCATION))) {
      return shapefileImportService.importShapefileProjectAsSpatialReference(shapefileProjectStream);
    }
  }
}