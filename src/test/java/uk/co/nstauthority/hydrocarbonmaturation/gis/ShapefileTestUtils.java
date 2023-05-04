package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.util.ResourceUtils;
import uk.co.nstauthority.hydrocarbonmaturation.gis.esri.ShapefileGeometryLoader;

public class ShapefileTestUtils {
  public static final String CLASSPATH_SHAPEFILES = "classpath:shapefiles";
  static final String INVALID_SHAPEFILE_ZIP_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/missing.zip";
  static final String SHAPEFILE_ZIP_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.zip";
  static final String SHAPEFILE_PROJECT_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.prj";
  static final String SHAPEFILE_DBASE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.dbf";
  static final String UNSUPPORTED_SHAPEFILE_PROJECT_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/unsupported.prj";
  static final String PROJCS_SHAPEFILE_PROJECT_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/osgb36_ng.prj";
  static final String NOT_SIMPLE_OGC_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/not_simple_ogc.shp";
  static final String NOT_SIMPLE_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/not_simple.shp";
  static final String SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.shp";
  static final String EMPTY_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/empty.shp";
  static final String CORRUPT_SHAPEFILE_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/corrupt.shp";
  static final String GEOJSON_RESOURCE_LOCATION = CLASSPATH_SHAPEFILES + "/test.json";

  static FileInputStream getFileInputStream(String shapefileResourceLocation) throws FileNotFoundException {
    return new FileInputStream(ResourceUtils.getFile(shapefileResourceLocation));
  }

  static String getStreamAsString(String resourceLocation) throws IOException {
    try (var jsonStream = ShapefileTestUtils.getFileInputStream(resourceLocation)) {
      return new String(jsonStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  static ShapefileGeometryLoader getStreamAsShapefileGeometryLoader(String shapefileResourceLocation) throws IOException {
    try (var shapefileStream = ShapefileTestUtils.getFileInputStream(shapefileResourceLocation)) {
      return new ShapefileGeometryLoader(shapefileStream);
    }
  }
}