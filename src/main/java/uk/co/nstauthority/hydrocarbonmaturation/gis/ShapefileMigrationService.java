package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShapefileMigrationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ShapefileMigrationService.class);
  public static final String PID = "pid";
  private final ShapefileValidationService shapefileValidationService;
  private final ShapefileZipService shapefileZipService;
  private final ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;

  @Autowired
  public ShapefileMigrationService(ShapefileValidationService shapefileValidationService,
                                   ShapefileZipService shapefileZipService,
                                   ShapefileMigrationProgressRepository shapefileMigrationProgressRepository) {
    this.shapefileValidationService = shapefileValidationService;
    this.shapefileZipService = shapefileZipService;
    this.shapefileMigrationProgressRepository = shapefileMigrationProgressRepository;
  }

  @Transactional
  public void migrateLegacyShapefile(ShapefileZipContents shapefileZipContents) {

    if (!shapefileZipService.hasRequiredFiles(shapefileZipContents)) {
      throw new IllegalArgumentException("Migration shapefile zip does not contain required files");
    }

    if (!shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(shapefileZipContents)) {
      throw new IllegalArgumentException("Migration shapefile zip contains unsupported coordinate reference system");
    }

    var shapefileAttributes = shapefileZipContents.attributes();
    var geometries = shapefileZipContents.geometries();

    if (shapefileAttributes.rows().size() != geometries.size()) {
      throw new IllegalArgumentException("The number of geometries does not match the number of shapefile attributes");
    }

    var polygonIdIndex = shapefileAttributes.getHeaderIndex(PID)
        .orElseThrow(() -> new IllegalArgumentException("Shapefile attributes does not contain %s header".formatted(PID)));

    for (var i = 0; i < geometries.size(); i++) {
      var geometry = geometries.get(i);
      var shapefileAttributeRow = shapefileAttributes.rows().get(i);

      var polygonId = shapefileAttributeRow.get(polygonIdIndex);

      validateAndMigrateGeometry(i, shapefileZipContents.thresholds(), geometry, polygonId);
    }
  }

  private void validateAndMigrateGeometry(int index, SpatialReference thresholds, Geometry geometry, String polygonId) {
    var migrationProgress = shapefileMigrationProgressRepository.findByPolygonId(polygonId)
        .orElseGet(() -> {
          var progress = new ShapefileMigrationProgress();
          progress.setPolygonId(polygonId);

          return progress;
        });

    if (migrationProgress.getStatus() == ShapefileMigrationStatus.COMPLETED) {
      LOGGER.info("Geometry already migrated for polygon {} at index {}", polygonId, index);
      return;
    }

    migrationProgress.setMigrationRecordIndex(index);

    if (!padiResDataHeaderExists(polygonId)) {
      migrationProgress.setStatus(ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_FOUND);
      shapefileMigrationProgressRepository.save(migrationProgress);
      return;
    }

    var geometryIsValid = shapefileValidationService.isGeometryValid(thresholds, geometry);
    if (!geometryIsValid) {

      migrationProgress.setStatus(ShapefileMigrationStatus.FAILED_GEOMETRY_NOT_VALID);
      shapefileMigrationProgressRepository.save(migrationProgress);
      return;
    }

    // TODO HM-537 convert geometry to GeoJSON, store on S3 and save as supporting document

    migrationProgress.setStatus(ShapefileMigrationStatus.COMPLETED);
    shapefileMigrationProgressRepository.save(migrationProgress);
  }

  private boolean padiResDataHeaderExists(String polygonId) {
    return shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(polygonId);
  }
}
