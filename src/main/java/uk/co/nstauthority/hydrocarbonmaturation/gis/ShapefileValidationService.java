package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorSimplify;
import com.esri.core.geometry.OperatorSimplifyOGC;
import com.esri.core.geometry.SpatialReference;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.validation.ValidationResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
class ShapefileValidationService {
  public static final String SHAPEFILE_DOES_NOT_CONTAIN_REQUIRED_FILES = "Shapefile does not contain required files.";
  public static final String SHAPEFILE_CONTAINS_INVALID_GEOMETRIES = "Shapefile contains invalid geometries.";
  public static final UnaryOperator<String> shapefileContainsUnsupportedCoordinateReferenceSystemFunction =
      "Shapefile contains an unsupported coordinate reference system for an %s resource."::formatted;
  private final OperatorSimplifyOGC operatorSimplifyOgc;
  private final OperatorSimplify operatorSimplify;
  private final ShapefileZipService shapefileZipService;

  @Autowired
  ShapefileValidationService(OperatorSimplifyOGC operatorSimplifyOgc,
                             OperatorSimplify operatorSimplify,
                             ShapefileZipService shapefileZipService) {
    this.operatorSimplifyOgc = operatorSimplifyOgc;
    this.operatorSimplify = operatorSimplify;
    this.shapefileZipService = shapefileZipService;
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

  boolean validateHasSupportedCoordinateReferenceSystem(ShapefileZipContents shapefileZip) {
    return shapefileZip.coordinateReferenceSystem() != null;
  }

  boolean supportedCoordinateReferenceSystemByOnshoreOrOffshoreIsValid(ShapefileZipContents shapefileZip,
                                                                       OnshoreOffshore onshoreOffshore) {
    var supportedCoordinateReferenceSystem = shapefileZip.coordinateReferenceSystem();
    if (supportedCoordinateReferenceSystem == null) {
      return false;
    }

    return supportedCoordinateReferenceSystem.getOnshoreOffshore() == onshoreOffshore;
  }

  public ValidationResult validate(InputStream inputStream, OnshoreOffshore onshoreOffshore) {

    var errors = new ArrayList<String>();

    var shapefileZip = shapefileZipService.getShapefileZipContents(inputStream, "shapefile zip");
    if (!shapefileZipService.hasRequiredFiles(shapefileZip)) {
      errors.add(SHAPEFILE_DOES_NOT_CONTAIN_REQUIRED_FILES);
    }

    if (!supportedCoordinateReferenceSystemByOnshoreOrOffshoreIsValid(shapefileZip, onshoreOffshore)) {
      errors.add(shapefileContainsUnsupportedCoordinateReferenceSystemFunction
          .apply(onshoreOffshore.getDisplayName().toLowerCase()));
    }

    var shapefileGeometriesAreValid = areShapefileGeometriesValid(shapefileZip);

    if (!shapefileGeometriesAreValid) {
      errors.add(SHAPEFILE_CONTAINS_INVALID_GEOMETRIES);
    }

    if (!errors.isEmpty()) {
      var message = String.join(System.lineSeparator(), errors);
      return ValidationResult.error(message);
    }

    return ValidationResult.success();
  }

  private boolean areShapefileGeometriesValid(ShapefileZipContents shapefileZip) {
    return shapefileZip.geometries().stream()
        .allMatch(geometry -> isGeometryValid(shapefileZip.thresholds(), geometry));
  }
}
