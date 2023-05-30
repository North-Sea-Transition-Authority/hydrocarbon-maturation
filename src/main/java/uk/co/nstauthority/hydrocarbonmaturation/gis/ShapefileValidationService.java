package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorSimplify;
import com.esri.core.geometry.OperatorSimplifyOGC;
import com.esri.core.geometry.SpatialReference;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.FileUploadCustomValidator;

@Service
public class ShapefileValidationService implements FileUploadCustomValidator {
  public static final String SHAPEFILE_DOES_NOT_CONTAIN_REQUIRED_FILES = "Shapefile does not contain required files.";
  public static final String SHAPEFILE_CONTAINS_INVALID_GEOMETRIES = "Shapefile contains invalid geometries.";
  public static final String SHAPEFILE_CONTAINS_UNSUPPORTED_COORDINATE_REFERENCE_SYSTEM
      = "Shapefile contains unsupported coordinate reference system.";
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

  @Override
  public List<String> validateFileUpload(InputStream inputStream, String filename) {
    var errors = new ArrayList<String>();

    var shapefileZip = shapefileZipService.getShapefileZipContents(inputStream, filename);
    if (!shapefileZipService.hasRequiredFiles(shapefileZip)) {
      errors.add(SHAPEFILE_DOES_NOT_CONTAIN_REQUIRED_FILES);
    }

    if (!validateHasSupportedCoordinateReferenceSystem(shapefileZip)) {
      errors.add(SHAPEFILE_CONTAINS_UNSUPPORTED_COORDINATE_REFERENCE_SYSTEM);
    }

    var shapefileGeometriesAreValid = areShapefileGeometriesValid(shapefileZip);

    if (!shapefileGeometriesAreValid) {
      errors.add(SHAPEFILE_CONTAINS_INVALID_GEOMETRIES);
    }

    return errors;
  }

  private boolean areShapefileGeometriesValid(ShapefileZipContents shapefileZip) {
    return shapefileZip.geometries().stream()
        .allMatch(geometry -> isGeometryValid(shapefileZip.thresholds(), geometry));
  }
}
