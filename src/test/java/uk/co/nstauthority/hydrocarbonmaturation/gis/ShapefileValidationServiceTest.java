package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorSimplify;
import com.esri.core.geometry.OperatorSimplifyOGC;
import com.esri.core.geometry.SpatialReference;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class ShapefileValidationServiceTest {
  @Mock
  private OperatorSimplifyOGC operatorSimplifyOgc;
  @Mock
  private OperatorSimplify operatorSimplify;
  @Mock
  private SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService;
  @Mock
  private ShapefileZipService shapefileZipService;
  @Mock
  private ShapefileImportService shapefileImportService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @InjectMocks
  private ShapefileValidationService underTest;


  @Nested
  class WhenValidatingFileUpload {
    @Mock
    private InputStream zipInputStream;
    @Mock
    private SpatialReference spatialReferenceFromWkt;
    @Mock
    private Geometry geometry;
    @Mock
    private SubmissionDetail submissionDetail;
    @Mock
    private ResourceDetail resourceDetail;
    private final String WKT = "WKT";

    private static final String FILENAME = "shapefile zip";
    private ShapefileZipContents SHAPEFILE_ZIP_CONTENTS_VALID;
    private ShapefileZipContents SHAPEFILE_ZIP_CONTENTS_NO_CRS;
    private final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
      SHAPEFILE_ZIP_CONTENTS_VALID = new ShapefileZipContents(
          "SHAPEFILE_NAME",
          List.of(FILENAME),
          spatialReferenceFromWkt,
          SupportedCoordinateReferenceSystem.ED50,
          List.of(geometry),
          new ShapefileAttributes(List.of("headers"), List.of(List.of("rows")))
      );

      SHAPEFILE_ZIP_CONTENTS_NO_CRS = new ShapefileZipContents(
          "SHAPEFILE_NAME",
          List.of(FILENAME),
          spatialReferenceFromWkt,
          null,
          List.of(geometry),
          new ShapefileAttributes(List.of("headers"), List.of(List.of("rows")))
      );
    }

    @Test
    void shapefileDoesNotContainRequiredFiles() {
      when(shapefileZipService.hasRequiredFiles(any(ShapefileZipContents.class))).thenReturn(false);
      when(operatorSimplify.isSimpleAsFeature(geometry, spatialReferenceFromWkt, false, null, null))
          .thenReturn(true);
      when(shapefileZipService.getShapefileZipContents(zipInputStream, FILENAME)).thenReturn(SHAPEFILE_ZIP_CONTENTS_VALID);

      var result = underTest.validate(zipInputStream);

      assertThat(result.errorMessage())
          .isEqualTo(ShapefileValidationService.SHAPEFILE_DOES_NOT_CONTAIN_REQUIRED_FILES);
    }

    @Test
    void shapefileContainsInvalidGeometries() {

      when(shapefileZipService.hasRequiredFiles(any(ShapefileZipContents.class))).thenReturn(true);
      when(operatorSimplify.isSimpleAsFeature(geometry, spatialReferenceFromWkt, false, null, null))
          .thenReturn(false);
      when(shapefileZipService.getShapefileZipContents(zipInputStream, FILENAME)).thenReturn(SHAPEFILE_ZIP_CONTENTS_VALID);

      var result = underTest.validate(zipInputStream);

      assertThat(result.errorMessage())
          .isEqualTo(ShapefileValidationService.SHAPEFILE_CONTAINS_INVALID_GEOMETRIES);
    }
  }
}