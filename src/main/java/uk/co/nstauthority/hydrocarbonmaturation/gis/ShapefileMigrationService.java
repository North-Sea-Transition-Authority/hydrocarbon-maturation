package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@Service
public class ShapefileMigrationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ShapefileMigrationService.class);
  public static final String PID = "pid";
  public static final SupportingDocumentType SUPPORTING_DOCUMENT_TYPE =
      SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE;
  public static final String SHAPEFILE_ZIP_CONTENT_TYPE = "application/zip";
  private final ShapefileValidationService shapefileValidationService;
  private final ShapefileZipService shapefileZipService;
  private final ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;
  private final SupportingDocumentService supportingDocumentService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public ShapefileMigrationService(ShapefileValidationService shapefileValidationService,
                                   ShapefileZipService shapefileZipService,
                                   ShapefileMigrationProgressRepository shapefileMigrationProgressRepository,
                                   SupportingDocumentService supportingDocumentService,
                                   SubmissionDetailService submissionDetailService) {
    this.shapefileValidationService = shapefileValidationService;
    this.shapefileZipService = shapefileZipService;
    this.shapefileMigrationProgressRepository = shapefileMigrationProgressRepository;
    this.supportingDocumentService = supportingDocumentService;
    this.submissionDetailService = submissionDetailService;
  }

  @Transactional
  public void migrateShapefilesInDirectory(String migrationDirectoryLocation) throws FileNotFoundException {
    var migrationDirectory = ResourceUtils.getFile(migrationDirectoryLocation);
    for (var file : FileUtils.listFiles(migrationDirectory, new String[]{"zip"}, false)) {
      var shapefileZipContents = shapefileZipService.getShapefileZipContents(file);
      migrateLegacyShapefile(shapefileZipContents, file);
    }
  }

  @Transactional
  public void migrateLegacyShapefile(ShapefileZipContents shapefileZipContents, File shapefileZipFile) {

    checkShapefileOrThrow(shapefileZipContents);

    var polygonIdIndex = shapefileZipContents.attributes().getHeaderIndexOrThrow(PID);

    var geometry = shapefileZipContents.geometries().get(0);
    var shapefileAttributeRow = shapefileZipContents.attributes().rows().get(0);
    var polygonId = shapefileAttributeRow.get(polygonIdIndex);

    var migrationProgress = shapefileMigrationProgressRepository.findByPolygonId(polygonId)
        .orElseGet(() -> {
          var progress = new ShapefileMigrationProgress();
          progress.setPolygonId(polygonId);

          return progress;
        });

    if (migrationProgress.getStatus() == ShapefileMigrationStatus.COMPLETED) {
      LOGGER.info("Geometry already migrated for polygon {}", polygonId);
      return;
    }

    migrationProgress.setMigrationRecordIndex(0);

    if (!padiResDataHeaderExists(polygonId)) {
      migrationProgress.setStatus(ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_FOUND);
      shapefileMigrationProgressRepository.save(migrationProgress);
      return;
    }

    var submissionDetails = getSubmissionDetails(polygonId);

    if (submissionDetails.isEmpty()) {
      migrationProgress.setStatus(ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_LINKED_TO_SUBMISSION_DETAIL);
      shapefileMigrationProgressRepository.save(migrationProgress);
      return;
    }

    var geometryIsValid = shapefileValidationService.isGeometryValid(shapefileZipContents.thresholds(), geometry);
    if (!geometryIsValid) {

      migrationProgress.setStatus(ShapefileMigrationStatus.FAILED_GEOMETRY_NOT_VALID);
      shapefileMigrationProgressRepository.save(migrationProgress);
      return;
    }

    submissionDetails.forEach(submissionDetail ->
        supportingDocumentService.uploadedFile(submissionDetail, SUPPORTING_DOCUMENT_TYPE, shapefileZipFile.length(),
            shapefileZipContents.shapefileName(), SHAPEFILE_ZIP_CONTENT_TYPE, getShapefileZipFileInputStream(shapefileZipFile)));

    migrationProgress.setStatus(ShapefileMigrationStatus.COMPLETED);
    shapefileMigrationProgressRepository.save(migrationProgress);

  }

  private List<SubmissionDetail> getSubmissionDetails(String polygonId) {
    return shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(polygonId).stream()
        .map(submissionDetailService::getSubmissionDetailByLegacyResDataId)
        .flatMap(Optional::stream)
        .toList();
  }

  private void checkShapefileOrThrow(ShapefileZipContents shapefileZipContents) {
    if (!shapefileZipService.hasRequiredFiles(shapefileZipContents)) {
      throw new IllegalArgumentException("Migration shapefile zip does not contain required files");
    }

    if (!shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(shapefileZipContents)) {
      throw new IllegalArgumentException("Migration shapefile zip contains unsupported coordinate reference system");
    }

    var geometries = shapefileZipContents.geometries();
    if (geometries.size() != 1) {
      throw new IllegalArgumentException("Expected 1 geometry, found " + geometries.size());
    }

    var rows = shapefileZipContents.attributes().rows();
    if (rows.size() != 1) {
      throw new IllegalArgumentException("Expected 1 shapefile attribute record, found " + rows.size());
    }
  }

  private boolean padiResDataHeaderExists(String polygonId) {
    return shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(polygonId);
  }

  FileInputStream getShapefileZipFileInputStream(File shapefileZipFile) {
    try {
      return new FileInputStream(shapefileZipFile);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File not found for shapefile %s".formatted(shapefileZipFile.getName()), e);
    }
  }
}
