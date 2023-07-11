package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileMigrationService.SHAPEFILE_ZIP_CONTENT_TYPE;
import static uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileMigrationService.SUPPORTING_DOCUMENT_TYPE;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class ShapefileMigrationServiceTest {
  @Mock
  private ShapefileValidationService shapefileValidationService;
  @Mock
  private ShapefileZipService shapefileZipService;
  @Mock
  private ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;
  @Mock
  private SupportingDocumentService supportingDocumentService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private S3ClientService s3ClientService;
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

    @Test
    void doesNotHaveSupportedCoordinateReferenceSystem() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(false);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
          "Migration shapefile zip contains unsupported coordinate reference system"
      );
    }

    @Test
    void doesNotContainRecordsForAllGeometries() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS);
      when(shapefileZipService.hasRequiredFiles(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS)).thenReturn(true);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
          "The number of geometries does not match the number of shapefile attributes"
      );
    }

    @Test
    void doesNotContainRequiredHeader() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID);
      when(shapefileZipService.hasRequiredFiles(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID)).thenReturn(true);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()),
          "Shapefile attributes does not contain %s header".formatted(POLYGON_ID)
      );
    }

    @Test
    void alreadySuccessfullyMigrated() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      var shapefileMigrationProgress = new ShapefileMigrationProgress();
      shapefileMigrationProgress.setPolygonId(POLYGON_ID);
      shapefileMigrationProgress.setStatus(ShapefileMigrationStatus.COMPLETED);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.of(shapefileMigrationProgress));

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());

      verify(shapefileMigrationProgressRepository, never()).save(argumentCaptor.capture());
    }

    @Test
    void legacyDataNotFound() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(false);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()
      );

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
    void dataNotLinkedToSubmissionDetail() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
      when(submissionDetailService.getSubmissionDetailByLegacyResDataId(RES_DATA_ID)).thenReturn(Optional.empty());

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()
      );

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

    @Test
    void invalidGeometry() {
      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
      when(submissionDetailService.getSubmissionDetailByLegacyResDataId(RES_DATA_ID)).thenReturn(Optional.of(SUBMISSION_DETAIL));
      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(false);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length()
      );

      verify(shapefileMigrationProgressRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue())
          .extracting(
              ShapefileMigrationProgress::getPolygonId,
              ShapefileMigrationProgress::getStatus
          )
          .containsExactly(
              POLYGON_ID,
              ShapefileMigrationStatus.FAILED_GEOMETRY_NOT_VALID
          );
    }

    @Test
    void successMigration() {

      when(shapefileZipService.getShapefileZipContents(INPUT_STREAM, FILE_NAME)).thenReturn(VALID_SHAPEFILE_ZIP_CONTENTS);
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findResDatIdFromPadiResDataHeaderByPolygonId(POLYGON_ID)).thenReturn(List.of(RES_DATA_ID));
      when(submissionDetailService.getSubmissionDetailByLegacyResDataId(RES_DATA_ID)).thenReturn(Optional.of(SUBMISSION_DETAIL));
      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(true);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(INPUT_STREAM, FILE_NAME, FILE.length());

      verify(supportingDocumentService).uploadedFile(eq(SUBMISSION_DETAIL), eq(SUPPORTING_DOCUMENT_TYPE), anyLong(), anyString(),eq(SHAPEFILE_ZIP_CONTENT_TYPE), eq(INPUT_STREAM));
      verify(shapefileMigrationProgressRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue())
          .extracting(
              ShapefileMigrationProgress::getPolygonId,
              ShapefileMigrationProgress::getStatus
          )
          .containsExactly(
              POLYGON_ID,
              ShapefileMigrationStatus.COMPLETED
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

    @BeforeEach
    void setUp() {
      underTestSpy = spy(underTest);
    }

    @Test
    void verifyCalls() throws IOException {
      var s3ObjectSummary1 = mockS3ObjectSummary(KEY, SIZE);
      var s3ObjectSummary2 = mockS3ObjectSummary("KEY");
      when(s3ClientService.getDefaultBucketName()).thenReturn(BUCKET_NAME);
      when(s3ClientService.getFileObjectsInBucket(BUCKET_NAME)).thenReturn(List.of(s3ObjectSummary1, s3ObjectSummary2));
      when(s3ClientService.fetchFile(BUCKET_NAME, KEY)).thenReturn(INPUT_STREAM);
      doReturn(BYTE_ARRAY_INPUT_STREAM).when(underTestSpy).getByteArrayInputStream(INPUT_STREAM);
      doNothing().when(underTestSpy).migrateLegacyShapefile(BYTE_ARRAY_INPUT_STREAM, KEY, SIZE);

      underTestSpy.migrateShapefilesFromS3();

      verify(underTestSpy).migrateLegacyShapefile(BYTE_ARRAY_INPUT_STREAM, KEY, SIZE);
    }

    private S3ObjectSummary mockS3ObjectSummary(String key, Long size) {
      var s3ObjectSummary = mockS3ObjectSummary(key);
      when(s3ObjectSummary.getSize()).thenReturn(size);
      return s3ObjectSummary;
    }

    private S3ObjectSummary mockS3ObjectSummary(String key) {
      var s3ObjectSummary = mock(S3ObjectSummary.class);
      when(s3ObjectSummary.getKey()).thenReturn(key);
      return s3ObjectSummary;
    }
  }
}