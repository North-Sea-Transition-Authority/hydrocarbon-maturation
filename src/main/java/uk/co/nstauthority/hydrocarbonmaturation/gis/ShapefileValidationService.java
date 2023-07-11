package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorSimplify;
import com.esri.core.geometry.OperatorSimplifyOGC;
import com.esri.core.geometry.SpatialReference;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.FileUploadCustomValidator;

@Service
public class ShapefileValidationService implements FileUploadCustomValidator {
  public static final String SHAPEFILE_DOES_NOT_CONTAIN_REQUIRED_FILES = "Shapefile does not contain required files.";
  public static final String SHAPEFILE_CONTAINS_INVALID_GEOMETRIES = "Shapefile contains invalid geometries.";
  public static final UnaryOperator<String> shapefileContainsUnsupportedCoordinateReferenceSystemFunction =
      "Shapefile contains unsupported coordinate reference system for %s."::formatted;
  private final OperatorSimplifyOGC operatorSimplifyOgc;
  private final OperatorSimplify operatorSimplify;
  private final ShapefileZipService shapefileZipService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  ShapefileValidationService(OperatorSimplifyOGC operatorSimplifyOgc,
                             OperatorSimplify operatorSimplify,
                             ShapefileZipService shapefileZipService,
                             ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                             SubmissionDetailService submissionDetailService) {
    this.operatorSimplifyOgc = operatorSimplifyOgc;
    this.operatorSimplify = operatorSimplify;
    this.shapefileZipService = shapefileZipService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
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

  @Override
  public List<String> validateFileUpload(InputStream inputStream, String filename, UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);

    var errors = new ArrayList<String>();

    var shapefileZip = shapefileZipService.getShapefileZipContents(inputStream, filename);
    if (!shapefileZipService.hasRequiredFiles(shapefileZip)) {
      errors.add(SHAPEFILE_DOES_NOT_CONTAIN_REQUIRED_FILES);
    }

    if (!supportedCoordinateReferenceSystemByOnshoreOrOffshoreIsValid(shapefileZip, resourceDetail.getOnshoreOffshore())) {
      var onshoreOffshoreName = resourceDetail.getOnshoreOffshore().getDisplayName();
      errors.add(shapefileContainsUnsupportedCoordinateReferenceSystemFunction.apply(onshoreOffshoreName));
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
