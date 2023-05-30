package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShapefileMigrationServiceTest {
  @Mock
  private ShapefileValidationService shapefileValidationService;
  @Mock
  private ShapefileZipService shapefileZipService;
  @Mock
  private ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;
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
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(false);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(VALID_SHAPEFILE_ZIP_CONTENTS),
          "Migration shapefile zip does not contain required files"
      );
    }

    @Test
    void doesNotHaveSupportedCoordinateReferenceSystem() {
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(false);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(VALID_SHAPEFILE_ZIP_CONTENTS),
          "Migration shapefile zip contains unsupported coordinate reference system"
      );
    }

    @Test
    void doesNotContainRecordsForAllGeometries() {
      when(shapefileZipService.hasRequiredFiles(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS)).thenReturn(true);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INVALID_SHAPEFILE_ZIP_CONTENTS_LESS_RECORDS),
          "The number of geometries does not match the number of shapefile attributes"
      );
    }

    @Test
    void doesNotContainRequiredHeader() {
      when(shapefileZipService.hasRequiredFiles(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID)).thenReturn(true);
      assertThrows(IllegalArgumentException.class, () ->
          underTest.migrateLegacyShapefile(INVALID_SHAPEFILE_ZIP_CONTENTS_NO_PID),
          "Shapefile attributes does not contain %s header".formatted(POLYGON_ID)
      );
    }

    @Test
    void alreadySuccessfullyMigrated() {
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      var shapefileMigrationProgress = new ShapefileMigrationProgress();
      shapefileMigrationProgress.setPolygonId(POLYGON_ID);
      shapefileMigrationProgress.setStatus(ShapefileMigrationStatus.COMPLETED);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.of(shapefileMigrationProgress));

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(VALID_SHAPEFILE_ZIP_CONTENTS);

      verify(shapefileMigrationProgressRepository, never()).save(argumentCaptor.capture());
    }

    @Test
    void legacyDataNotFound() {
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(false);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(VALID_SHAPEFILE_ZIP_CONTENTS);

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
    void invalidGeometry() {
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(false);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(VALID_SHAPEFILE_ZIP_CONTENTS);

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
      when(shapefileZipService.hasRequiredFiles(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileValidationService.validateHasSupportedCoordinateReferenceSystem(VALID_SHAPEFILE_ZIP_CONTENTS)).thenReturn(true);
      when(shapefileMigrationProgressRepository.findByPolygonId(POLYGON_ID)).thenReturn(Optional.empty());
      when(shapefileMigrationProgressRepository.padiResDataHeaderExistsByPolygonId(POLYGON_ID)).thenReturn(true);
      when(shapefileValidationService.isGeometryValid(VALID_SHAPEFILE_ZIP_CONTENTS.thresholds(), VALID_SHAPEFILE_ZIP_CONTENTS.geometries().get(0))).thenReturn(true);

      var argumentCaptor = ArgumentCaptor.forClass(ShapefileMigrationProgress.class);
      underTest.migrateLegacyShapefile(VALID_SHAPEFILE_ZIP_CONTENTS);

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
}