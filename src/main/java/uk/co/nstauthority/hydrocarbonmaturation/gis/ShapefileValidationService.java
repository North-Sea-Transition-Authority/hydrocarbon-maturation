package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorSimplify;
import com.esri.core.geometry.OperatorSimplifyOGC;
import com.esri.core.geometry.SpatialReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ShapefileValidationService {
  private final OperatorSimplifyOGC operatorSimplifyOgc;
  private final OperatorSimplify operatorSimplify;
  private final SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService;

  @Autowired
  ShapefileValidationService(OperatorSimplifyOGC operatorSimplifyOgc,
                             OperatorSimplify operatorSimplify,
                             SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService) {
    this.operatorSimplifyOgc = operatorSimplifyOgc;
    this.operatorSimplify = operatorSimplify;
    this.supportedCoordinateReferenceSystemService = supportedCoordinateReferenceSystemService;
  }

  /**
   * Check if a geometry is valid according to ESRI.
   */
  boolean isGeometryValid(SpatialReference spatialReference, Geometry geometry) {
    return operatorSimplify.isSimpleAsFeature(geometry, spatialReference, false, null, null);
  }

  /**
   * Check if a geometry is valid according to OGC specification.
   */
  boolean isGeometryValidStrict(SpatialReference spatialReference, Geometry geometry) {
    return operatorSimplifyOgc.isSimpleOGC(geometry, spatialReference, false, null, null);
  }

  boolean validateGeometryIsOfType(Geometry geometry, Geometry.Type expectType) {
    return geometry.getType() == expectType;
  }

  boolean validateWktHasSupportedCoordinateReferenceSystem(String wellKnownText) {
    var supportedCrsListFromWkt =
        supportedCoordinateReferenceSystemService.getSupportedCoordinateReferenceSystemFromWkt(wellKnownText);

    return supportedCrsListFromWkt != null;
  }
}
