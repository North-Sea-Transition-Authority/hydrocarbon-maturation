package uk.co.nstauthority.hydrocarbonmaturation.gis.esri;

import static org.assertj.core.api.Assertions.assertThat;

import com.esri.core.geometry.Geometry;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

class ShapefileGeometryLoaderTest {

  private static final String SHAPEFILE_RESOURCE_LOCATION = "classpath:shapefiles/test.shp";

  @Test
  void loadShapeFile_ImportPolygonGeometry() throws IOException {
    try (var stream = new FileInputStream(ResourceUtils.getFile(SHAPEFILE_RESOURCE_LOCATION))) {
      var shapefileGeometryLoader = new ShapefileGeometryLoader(stream);

      var shapefileHeader = shapefileGeometryLoader.getShapefileHeader();
      var geometryList = shapefileGeometryLoader.getGeometryList();

      assertThat(shapefileHeader)
          .extracting(
              ShapefileGeometryLoader.ShapefileHeader::fileLength,
              ShapefileGeometryLoader.ShapefileHeader::geometryType
          )
          .containsExactly(
              694,
              Geometry.Type.Polygon
          );

      var geometry = geometryList.get(0);

      assertThat(geometry).isNotNull();
      assertThat(geometry.getType()).isEqualTo(Geometry.Type.Polygon);
    }
  }
}