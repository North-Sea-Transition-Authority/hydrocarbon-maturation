package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.configuration.FileUploadProperties;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadResponse;
import uk.co.fivium.fileuploadlibrary.fds.UploadErrorType;
import uk.co.fivium.fileuploadlibrary.s3.S3Exception;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.ListS3FilesResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.RemiS3FileService;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3File;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCase;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseDocumentTypeService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class ShapefileMigrationServiceTest {
  @Mock
  private ShapefileValidationService shapefileValidationService;
  @Mock
  private ShapefileZipService shapefileZipService;
  @Mock
  private ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private RemiS3FileService remiS3FileService;
  @Mock
  private FileService fileService;
  @Mock
  private FileUploadProperties fileUploadProperties;
  @Mock
  private OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  @Mock
  private GeoJsonShapefileLinkService geoJsonShapefileLinkService;
  @Mock
  private OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @InjectMocks
  private ShapefileMigrationService underTest;

  @Nested
  class WhenMigratingLegacyShapefile {


    private static final String POLYGON_ID = "1";
    private static final ShapefileZipContents VALID_SHAPEFILE_ZIP_CONTENTS = getShapefileZipContents(List.of(mock(Geometry.class)),
        List.of("pid"), List.of(POLYGON_ID)
    );
    private static final ShapefileZipContents INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS = getShapefileZipContents(List.of(mock(Geometry.class), mock(Geometry.class)),
        List.of("pid"), List.of(POLYGON_ID)
    );
    private static final ShapefileZipContents INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID = getShapefileZipContents(List.of(mock(Geometry.class)),
        List.of("circle"), List.of(POLYGON_ID)
    );
    private static final String RESOURCE_NAME = "res name";
    private final int RES_DATA_ID = 1;
    private final String FILE_NAME = "FILE_NAME";
    private final long FILE_SIZE = 62;
    @Mock
    private File FILE;
    @Mock
    private SubmissionDetail SUBMISSION_DETAIL;
    @Mock
    private InputStream INPUT_STREAM;
    @Mock
    private InputStream shapefileZipInputStream;

    private static ShapefileZipContents getShapefileZipContents(List<Geometry> geometries,
                                                                List<String> shapefileAttributesHeaders,
                                                                List<String> shapefileAttributesRows) {
      return new ShapefileZipContents(
          "shapefile_name",
          List.of("filenames"),
          mock(SpatialReference.class),
          SupportedCoordinateReferenceSystem.WGS84,
          geometries,
          new ShapefileAttributes(shapefileAttributesHeaders, List.of(shapefileAttributesRows))
      );
    }

    @Test
    void doesNotHaveRequiredFiles() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(false);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
          "Migration shapefile zip does not contain required files"
      );
    }

//    @Test
//    void doesNotHaveSupportedCoordinateReferenceSystem() {
//      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
//      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(false);
//      assertThrows(IllegalArgumentException.class, () ->
//          underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
//          "Migration shapefile zip contains unsupported coordinate reference system"
//      );
//    }

    @Test
    void doesNotContainRecordsForAllGeometries() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS);
      when(shapefileZipService.hasRequiredFiles(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS)).thenReturn(true);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
          "The number of geometries does not match the number of shapefile attributes"
      );
    }

    @Test
    void doesNotContainRequiredHeader() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID);
      when(shapefileZipService.hasRequiredFiles(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID)).thenReturn(true);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
          "Shapefile attributes does not contain %s header".formatted(POLYGON_ID)
      );
    }

    @Test
    void alreadySuccessfullyMigrated() throws IOException {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      var shapefileMigrationProgress = new ShapefileMigrationProgress();
      shapefileMigrationProgress.setPolygonId(POLYGON_ID);
      shapefileMigrationProgress.setStatus(ShapefileMigrationStatus.COMPLETED);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.of(shapefileMigrationProgress));

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());

      verify(shapefileMigrationProgressRepository, never()).save(argumentCaptor.capture());
    }

    @Test
    void legacyDataNotFound() throws IOException {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(false);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());

      verify(shapefileMigrationProgressRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue())
          .extracting(
              ShapefileMigrationProgress::getPolygonId,
              ShapefileMigrationProgress::getStatus
          )
          .containsExactly(
              POLYGON_ID,
              ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_FOUND
          );
    }

    @Test
    void dataNotLinkedToSubmissionDetail() throws IOException {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
      when(submissionDetailService.getSubmissionDetailsByLegacyResDataId(RES_DATA_ID)).thenReturn(List.of());

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());

      verify(shapefileMigrationProgressRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue())
          .extracting(
              ShapefileMigrationProgress::getPolygonId,
              ShapefileMigrationProgress::getStatus
          )
          .containsExactly(
              POLYGON_ID,
              ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_LINKED_TO_SUBMISSION_DETAIL
          );
    }

//    @Test
//    void invalidGeometry() throws IOException {
//      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
//      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
//      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
//      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
//      when(submissionDetailService.getSubmissionDetailsByLegacyResDataId(RES_DATA_ID)).thenReturn(List.of(SUBMISSION_DETAIL));
//      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(false);
//
//      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
//      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());
//
//      verify(shapefileMigrationProgressRepository).save(argumentCaptor.capture());
//      assertThat(argumentCaptor.getValue())
//          .extracting(
//              ShapefileMigrationProgress::getPolygonId,
//              ShapefileMigrationProgress::getStatus
//          )
//          .containsExactly(
//              POLYGON_ID,
//              ShapefileMigrationStatus.FAILED_GEOMETRY_NOT_VALID
//          );
//    }

    @Test
    void failedToUploadFile() throws IOException {

      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      var fileUploadResponse = mock(FileUploadResponse.class);
      when(fileUploadResponse.getError()).thenReturn(UploadErrorType.MAX_FILE_SIZE_EXCEEDED.getErrorMessage());

      when(INPUT_STREAM.markSupported()).thenReturn(true);
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
      when(submissionDetailService.getSubmissionDetailsByLegacyResDataId(RES_DATA_ID)).thenReturn(List.of(SUBMISSION_DETAIL));
//      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(true);
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(SUBMISSION_DETAIL)).thenReturn(documentType);
      when(fileService.upload(any(Function.class))).thenReturn(fileUploadResponse);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());

      verify(shapefileMigrationProgressRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue())
          .extracting(
              ShapefileMigrationProgress::getPolygonId,
              ShapefileMigrationProgress::getStatus
          )
          .containsExactly(
              POLYGON_ID,
              ShapefileMigrationStatus.FAILED_TO_UPLOAD_FILE
          );
    }

    @Test
    void internalServerError() {

      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      var fileUploadResponse = mock(FileUploadResponse.class);
      when(fileUploadResponse.getError()).thenReturn(UploadErrorType.INTERNAL_SERVER_ERROR.getErrorMessage());

      when(INPUT_STREAM.markSupported()).thenReturn(true);
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
      when(submissionDetailService.getSubmissionDetailsByLegacyResDataId(RES_DATA_ID)).thenReturn(List.of(SUBMISSION_DETAIL));
//      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(true);
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(SUBMISSION_DETAIL)).thenReturn(documentType);
      when(fileService.upload(any(Function.class))).thenReturn(fileUploadResponse);

      assertThrows(IllegalArgumentException.class, () ->
              underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
          "Failed to upload shapefile file for polygon %s".formatted(POLYGON_ID));
    }

    @Test
    void successMigration() throws IOException {

      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      var fileUploadResponse = mock(FileUploadResponse.class);
      var fileId = UUID.randomUUID();
      when(fileUploadResponse.getFileId()).thenReturn(fileId);

      when(INPUT_STREAM.markSupported()).thenReturn(true);
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
//      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
      when(submissionDetailService.getSubmissionDetailsByLegacyResDataId(RES_DATA_ID)).thenReturn(List.of(SUBMISSION_DETAIL));
//      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(true);
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(SUBMISSION_DETAIL)).thenReturn(documentType);
      when(fileService.upload(any(Function.class))).thenReturn(fileUploadResponse);
      var uploadedFile = new UploadedFile();
      when(fileService.find(fileId)).thenReturn(Optional.of(uploadedFile));
      var resourceDetail = new ResourceDetail();
      resourceDetail.setResourceName(RESOURCE_NAME);
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(SUBMISSION_DETAIL))
          .thenReturn(Optional.of(resourceDetail));

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      var maxCaseArgumentCaptor = ArgumentCaptor.forClass(OutlineForMaximumCase.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());

      assertEquals(
          uploadedFile.getDescription(),
          "Outline for maximum case for %s".formatted(resourceDetail.getResourceName())
      );
      verify(shapefileMigrationProgressRepository).save(argumentCaptor.capture());
      verify(geoJsonShapefileLinkService).storeShapefileAsGeoJson(uploadedFile, VALID_SHAPEFILE_ZIP_CONTENTS);
      verify(outlineForMaximumCasePersistenceService).saveOutlineForMaximumCase(maxCaseArgumentCaptor.capture());
      assertThat(argumentCaptor.getValue())
          .extracting(
              ShapefileMigrationProgress::getPolygonId,
              ShapefileMigrationProgress::getStatus
          )
          .containsExactly(
              POLYGON_ID,
              ShapefileMigrationStatus.COMPLETED
          );
      assertThat(maxCaseArgumentCaptor.getValue())
          .extracting(
              OutlineForMaximumCase::getSubmissionDetail,
              OutlineForMaximumCase::getHasVerifiedShapefile
          )
          .containsExactly(
              SUBMISSION_DETAIL,
              true
          );
    }
  }

  @Nested
  class WhenMigratingShapefilesFromS3 {
    private ShapefileMigrationService underTestSpy;
    @Mock
    private InputStream INPUT_STREAM;
    @Mock
    private ByteArrayInputStream BYTE_ARRAY_INPUT_STREAM;
    private final String BUCKET_NAME = "BUCKET_NAME";
    private final String KEY = ShapefileMigrationService.VIRTUAL_FOLDER + "KEY.zip";
    private final Long SIZE = 1L;
    private final FileUploadProperties.S3 S3 = new FileUploadProperties.S3(
        "access_key",
        "secret_token",
        "endpoint",
        "signing_region",
        BUCKET_NAME,
        true,
        new FileUploadProperties.S3.Proxy("host", 4818));

    @BeforeEach
    void setUp() {
      underTestSpy = spy(underTest);
    }

    @Test
    void verifyCalls_MigratesZipsOnly() throws IOException, S3Exception {
      var s3File1 = new S3File(KEY, SIZE);
      var s3File2 = new S3File("KEY", 1L);
      var continuationToken = "token";
      when(fileUploadProperties.s3()).thenReturn(S3);
      var listS3FilesResult = new ListS3FilesResult(false, "token2", List.of(s3File1, s3File2));
      when(remiS3FileService.listS3Files(BUCKET_NAME, ShapefileMigrationService.VIRTUAL_FOLDER, continuationToken)).thenReturn(listS3FilesResult);
      when(remiS3FileService.downloadFile(BUCKET_NAME, KEY)).thenReturn(INPUT_STREAM);
      doReturn(BYTE_ARRAY_INPUT_STREAM).when(underTestSpy).getByteArrayInputStream(INPUT_STREAM);
      doNothing().when(underTestSpy).migrateLegacyShapefile(BYTE_ARRAY_INPUT_STREAM, KEY, SIZE);

      underTestSpy.migrateShapefilesFromS3(continuationToken);

      verify(underTestSpy).migrateLegacyShapefile(BYTE_ARRAY_INPUT_STREAM, KEY, SIZE);
    }

  }
}