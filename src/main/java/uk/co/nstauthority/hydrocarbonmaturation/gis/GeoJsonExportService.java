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

  public String exportShapefileAsGeoJson(SpatialReference spatialReference, List<Geometry> geometries) {

    if (geometries.isEmpty()) {
      throw new IllegalArgumentException("Could not find geometries in shapefile");
    }

    var geometry = getFirstGeometryOrFlattenMultipleGeometries(spatialReference, geometries);

    return exportGeometryAsGeoJson(spatialReference, geometry);
  }

  private Geometry getFirstGeometryOrFlattenMultipleGeometries(SpatialReference spatialReference, List<Geometry> geometries) {
    if (geometries.size() == 1) {
      return geometries.get(0);
    }

    var geometriesArray = geometries.toArray(Geometry[]::new);
    return getUnionOfGeometries(spatialReference, geometriesArray);
  }

  public String exportGeometryAsGeoJson(SpatialReference spatialReference, Geometry geometry) {
    // TODO create a spatial reference that contains a WKID, otherwise the export fails without the geoJsonExportSkipCRS flag
    return exportToGeoJson.execute(GeoJsonExportFlags.geoJsonExportSkipCRS, spatialReference, geometry);
  }

  private Geometry getUnionOfGeometries(SpatialReference spatialReference, Geometry[] geometriesArray) {
    var geometryCursor = new SimpleGeometryCursor(geometriesArray);
    var resultCursor = operatorUnion.execute(geometryCursor, spatialReference, null);
    return resultCursor.next();
  }
}
