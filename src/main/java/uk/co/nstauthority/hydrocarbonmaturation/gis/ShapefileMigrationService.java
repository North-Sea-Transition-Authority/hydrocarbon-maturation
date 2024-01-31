package uk.co.nstauthority.hydrocarbonmaturation.gis;

import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uk.co.fivium.fileuploadlibrary.configuration.FileUploadProperties;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.s3.S3FileService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.MultipartFileInputStream;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Service
public class ShapefileMigrationService {
  public static final String PID = "pid";
  public static final String SHAPEFILE_ZIP_CONTENT_TYPE = "application/zip";
  public static final String VIRTUAL_FOLDER = "migration/shapefiles/";
  public static final FileDocumentType SUPPORTING_DOCUMENT_TYPE =
      FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
  private static final Logger LOGGER = LoggerFactory.getLogger(ShapefileMigrationService.class);
  private final ShapefileValidationService shapefileValidationService;
  private final ShapefileZipService shapefileZipService;
  private final ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;
  private final SubmissionDetailService submissionDetailService;
  private final S3FileService s3FileService;
  private final FileService fileService;
  private final FileUploadProperties fileUploadProperties;

  @Autowired
  public ShapefileMigrationService(ShapefileValidationService shapefileValidationService,
                                   ShapefileZipService shapefileZipService,
                                   ShapefileMigrationProgressRepository shapefileMigrationProgressRepository,
                                   SubmissionDetailService submissionDetailService,
                                   S3FileService s3FileService, FileService fileService,
                                   FileUploadProperties fileUploadProperties) {
    this.shapefileValidationService = shapefileValidationService;
    this.shapefileZipService = shapefileZipService;
    this.shapefileMigrationProgressRepository = shapefileMigrationProgressRepository;
    this.submissionDetailService = submissionDetailService;
    this.s3FileService = s3FileService;
    this.fileService = fileService;
    this.fileUploadProperties = fileUploadProperties;
  }

  void migrateShapefilesInDirectory(String migrationDirectoryLocation) throws IOException {
    var migrationDirectory = ResourceUtils.getFile(migrationDirectoryLocation);
    for (var file : FileUtils.listFiles(migrationDirectory, new String[]{"zip"}, false)) {
      migrateLegacyShapefile(getByteArrayInputStream(getShapefileZipFileInputStream(file)), file.getName(), file.length());
    }
  }

  @Transactional
  public void migrateShapefilesFromS3() {
    // TODO refactor in HM-875
    var bucketName = fileUploadProperties.s3().defaultBucket();
    var fileKeys = List.of(); //s3FileService.getFileObjectsInBucket(defaultBucket);

    //    fileKeys.stream()
    //        .filter(fileObject -> fileObject.getKey().startsWith(VIRTUAL_FOLDER) && fileObject.getKey().endsWith(".zip"))
    //        .forEach(fileObject -> {
    //          try (var inputStream = s3FileService.downloadFile(bucketName, fileObject.getKey())) {
    //            var byteArrayInputStream = getByteArrayInputStream(inputStream);
    //            migrateLegacyShapefile(byteArrayInputStream, fileObject.getKey(), fileObject.getSize());
    //          } catch (IOException e) {
    //            throw new IllegalArgumentException("Could not fetch file from S3 or migrate shapefile", e);
    //          }
    //        });
  }

  @Transactional
  public void migrateLegacyShapefile(InputStream shapefileZipInputStream, String fileName, long fileSize) {
    var shapefileZipContents = shapefileZipService.getShapefileZipContents(shapefileZipInputStream, fileName);

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

    rewindShapefileInputStream(shapefileZipInputStream, fileName);

    submissionDetails.forEach(submissionDetail -> {
      var multipartFileInputStream = new MultipartFileInputStream(fileName, SHAPEFILE_ZIP_CONTENT_TYPE, fileSize,
          shapefileZipInputStream);

      fileService.upload(builder -> builder
          .withMultipartFile(multipartFileInputStream)
          .withUsage(submissionDetail.getId().toString(), SubmissionDetail.class.getSimpleName(), SUPPORTING_DOCUMENT_TYPE.name())
          .build());
    });

    migrationProgress.setStatus(ShapefileMigrationStatus.COMPLETED);
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
