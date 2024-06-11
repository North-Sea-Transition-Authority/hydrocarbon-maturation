package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uk.co.fivium.fileuploadlibrary.configuration.FileUploadProperties;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.fds.UploadErrorType;
import uk.co.fivium.fileuploadlibrary.s3.S3Exception;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUsageType;
import uk.co.nstauthority.hydrocarbonmaturation.file.MultipartFileInputStream;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.ListS3FilesResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.RemiS3FileService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseDocumentTypeService;

@Service
public class ShapefileMigrationService {
  public static final String PID = "pid";
  public static final String SHAPEFILE_ZIP_CONTENT_TYPE = "application/zip";
  public static final String VIRTUAL_FOLDER = "migration/shapefiles/";
  private static final Logger LOGGER = LoggerFactory.getLogger(ShapefileMigrationService.class);
  private final ShapefileValidationService shapefileValidationService;
  private final ShapefileZipService shapefileZipService;
  private final ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;
  private final SubmissionDetailService submissionDetailService;
  private final RemiS3FileService remiS3FileService;
  private final FileService fileService;
  private final FileUploadProperties fileUploadProperties;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  private final GeoJsonShapefileLinkService geoJsonShapefileLinkService;

  @Autowired
  public ShapefileMigrationService(ShapefileValidationService shapefileValidationService,
                                   ShapefileZipService shapefileZipService,
                                   ShapefileMigrationProgressRepository shapefileMigrationProgressRepository,
                                   SubmissionDetailService submissionDetailService,
                                   RemiS3FileService remiS3FileService,
                                   FileService fileService,
                                   FileUploadProperties fileUploadProperties,
                                   OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService,
                                   GeoJsonShapefileLinkService geoJsonShapefileLinkService) {
    this.shapefileValidationService = shapefileValidationService;
    this.shapefileZipService = shapefileZipService;
    this.shapefileMigrationProgressRepository = shapefileMigrationProgressRepository;
    this.submissionDetailService = submissionDetailService;
    this.remiS3FileService = remiS3FileService;
    this.fileService = fileService;
    this.fileUploadProperties = fileUploadProperties;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
    this.geoJsonShapefileLinkService = geoJsonShapefileLinkService;
  }

  @VisibleForTesting
  void migrateShapefilesInDirectory(String migrationDirectoryLocation) throws IOException {
    var migrationDirectory = ResourceUtils.getFile(migrationDirectoryLocation);
    for (var file : FileUtils.listFiles(migrationDirectory, new String[]{"zip"}, false)) {
      migrateLegacyShapefile(getByteArrayInputStream(getShapefileZipFileInputStream(file)), file.getName(), file.length());
    }
  }

  ListS3FilesResult migrateShapefilesFromS3(@Nullable String continuationToken) throws S3Exception, IOException {
    var bucketName = fileUploadProperties.s3().defaultBucket();
    var listS3FilesResult = remiS3FileService.listS3Files(bucketName, VIRTUAL_FOLDER, continuationToken);
    var fileKeys = listS3FilesResult.s3Files().stream()
        .filter(s3File -> s3File.key().startsWith(VIRTUAL_FOLDER) && s3File.key().endsWith(".zip"))
        .toList();

    for (var s3File : fileKeys) {
      var filename = s3File.key();
      var shapefileMigrationProgress = shapefileMigrationProgressRepository
          .findByFilenameAndStatus(filename, ShapefileMigrationStatus.COMPLETED);
      if (shapefileMigrationProgress.isPresent()) {
        LOGGER.info("Geometry already migrated for filename {}", filename);
        continue;
      }
      try (var byteArrayInputStream = getByteArrayInputStream(remiS3FileService.downloadFile(bucketName, filename))) {
        migrateLegacyShapefile(byteArrayInputStream, filename, s3File.size());
      } catch (IOException e) {
        throw new IOException("Failed to migrate shapefile %s".formatted(filename), e);
      }
    }

    return listS3FilesResult;
  }

  @Transactional
  public void migrateLegacyShapefile(InputStream shapefileZipInputStream, String fileName, long fileSize) throws IOException {
    var shapefileZipContents = shapefileZipService.getShapefileZipContents(shapefileZipInputStream, fileName);

    checkShapefileOrThrow(shapefileZipContents);

    var polygonIdIndex = shapefileZipContents.attributes().getHeaderIndexOrThrow(PID);

    //    var geometry = shapefileZipContents.geometries().get(0);
    var shapefileAttributeRow = shapefileZipContents.attributes().rows().get(0);
    var polygonId = shapefileAttributeRow.get(polygonIdIndex);

    var migrationProgress = shapefileMigrationProgressRepository.findByPolygonId(polygonId)
        .orElseGet(() -> {
          var progress = new ShapefileMigrationProgress();
          progress.setPolygonId(polygonId);

          return progress;
        });

    migrationProgress.setFilename(fileName);

    if (migrationProgress.getStatus() == ShapefileMigrationStatus.COMPLETED) {
      LOGGER.info("Geometry already migrated for polygon {}", polygonId);
      return;
    }

    if (!padiResDataHeaderExists(polygonId)) {
      setProgressStatusAndSave(migrationProgress, ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_FOUND);
      return;
    }

    var submissionDetails = getSubmissionDetails(polygonId);

    if (submissionDetails.isEmpty()) {
      setProgressStatusAndSave(migrationProgress, ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_LINKED_TO_SUBMISSION_DETAIL);
      return;
    }

    //    var geometryIsValid = shapefileValidationService.isGeometryValid(shapefileZipContents.thresholds(), geometry);
    //    if (!geometryIsValid) {
    //      var invalidReason = shapefileValidationService.getGeometryInvalidReason(shapefileZipContents.thresholds(), geometry);
    //      migrationProgress.setInvalidGeometryReason(invalidReason);
    //      setProgressStatusAndSave(migrationProgress, ShapefileMigrationStatus.FAILED_GEOMETRY_NOT_VALID);
    //
    //      LOGGER.info("Shapefile geometry not valid for polygon {} with reason {}", polygonId, invalidReason);
    //      return;
    //    }

    rewindShapefileInputStream(shapefileZipInputStream, fileName);

    for (var submissionDetail : submissionDetails) {
      var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail);
      var multipartFileInputStream =
          new MultipartFileInputStream(fileName, SHAPEFILE_ZIP_CONTENT_TYPE, fileSize, shapefileZipInputStream);

      var fileUploadResponse = fileService.upload(builder -> builder
          .withMultipartFile(multipartFileInputStream)
          .withUsage(submissionDetail.getId().toString(), FileUsageType.SUBMISSION_DETAIL.getUsageType(), documentType.name())
          .build());

      if (fileUploadResponse.getError() != null) {
        var message = "Failed to upload shapefile file for polygon %s".formatted(polygonId);

        if (UploadErrorType.INTERNAL_SERVER_ERROR.getErrorMessage().equals(fileUploadResponse.getError())) {
          throw new IllegalArgumentException(message);
        }

        setProgressStatusAndSave(migrationProgress, ShapefileMigrationStatus.FAILED_TO_UPLOAD_FILE);
        LOGGER.error(message);
        return;
      }

      var uploadedFile = fileService.find(fileUploadResponse.getFileId()).orElseThrow(() ->
          new IllegalStateException("Uploaded file not found for submission detail %s".formatted(submissionDetail.getId())));
      geoJsonShapefileLinkService.storeShapefileAsGeoJson(uploadedFile, shapefileZipContents);
    }

    setProgressStatusAndSave(migrationProgress, ShapefileMigrationStatus.COMPLETED);
    LOGGER.info("Shapefile migrated for polygon {}", polygonId);
  }

  private void setProgressStatusAndSave(ShapefileMigrationProgress migrationProgress,
                                        ShapefileMigrationStatus shapefileMigrationStatus) {
    migrationProgress.setStatus(shapefileMigrationStatus);
    shapefileMigrationProgressRepository.save(migrationProgress);
  }

  private static void rewindShapefileInputStream(InputStream shapefileZipInputStream, String fileName) {
    try {
      shapefileZipInputStream.reset();
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not reset shapefile zip input stream for file %s".formatted(fileName), e);
    }
  }

  private List<SubmissionDetail> getSubmissionDetails(String polygonId) {
    return shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(polygonId).stream()
        .flatMap((var legacyResDataId) -> submissionDetailService.getSubmissionDetailsByLegacyResDataId(legacyResDataId).stream())
        .toList();
  }

  private void checkShapefileOrThrow(ShapefileZipContents shapefileZipContents) {
    if (!shapefileZipService.hasRequiredFiles(shapefileZipContents)) {
      throw new IllegalArgumentException("Migration shapefile zip does not contain required files");
    }

    //    if (!shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(shapefileZipContents)) {
    //      throw new IllegalArgumentException("Migration shapefile zip contains unsupported coordinate reference system");
    //    }

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

  private FileInputStream getShapefileZipFileInputStream(File shapefileZipFile) {
    try {
      return new FileInputStream(shapefileZipFile);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File not found for shapefile %s".formatted(shapefileZipFile.getName()), e);
    }
  }

  ByteArrayInputStream getByteArrayInputStream(InputStream inputStream) throws IOException {
    return new ByteArrayInputStream(inputStream.readAllBytes());
  }
}
