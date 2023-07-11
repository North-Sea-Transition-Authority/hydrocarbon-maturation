package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.GeoJsonExportFlags;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorExportToGeoJson;
import com.esri.core.geometry.OperatorUnion;
import com.esri.core.geometry.SimpleGeometryCursor;
import com.esri.core.geometry.SpatialReference;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class GeoJsonExportService {
  private final OperatorExportToGeoJson exportToGeoJson;
  private final OperatorUnion operatorUnion;

  @Autowired
  GeoJsonExportService(OperatorExportToGeoJson exportToGeoJson,
                       OperatorUnion operatorUnion) {
    this.exportToGeoJson = exportToGeoJson;
    this.operatorUnion = operatorUnion;
  }

  public String exportGeometryAsGeoJson(SpatialReference thresholds,
                                        SupportedCoordinateReferenceSystem coordinateReferenceSystem,
                                        List<Geometry> geometries) {

    if (geometries.isEmpty()) {
      throw new IllegalArgumentException("Could not find geometries in shapefile");
    }

    var geometry = getFirstGeometryOrFlattenMultipleGeometries(thresholds, geometries);

    return exportGeometryAsGeoJson(coordinateReferenceSystem, geometry);
  }

  private Geometry getFirstGeometryOrFlattenMultipleGeometries(SpatialReference thresholds, List<Geometry> geometries) {
    if (geometries.size() == 1) {
      return geometries.get(0);
    }

    var geometriesArray = geometries.toArray(Geometry[]::new);
    return getUnionOfGeometries(thresholds, geometriesArray);
  }

  public String exportGeometryAsGeoJson(SupportedCoordinateReferenceSystem coordinateReferenceSystem, Geometry geometry) {
    var crs = coordinateReferenceSystem.toSpatialReference();
    return exportToGeoJson.execute(GeoJsonExportFlags.geoJsonExportDefaults, crs, geometry);
  }

  private Geometry getUnionOfGeometries(SpatialReference thresholds, Geometry[] geometriesArray) {
    var geometryCursor = new SimpleGeometryCursor(geometriesArray);
    var resultCursor = operatorUnion.execute(geometryCursor, thresholds, null);
    return resultCursor.next();
  }
}
