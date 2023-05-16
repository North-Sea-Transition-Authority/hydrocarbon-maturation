package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;

@ExtendWith(MockitoExtension.class)
class ShapefileConversionServiceTest {

  @Mock
  private ShapefileZipService shapefileZipService;
  @Mock
  private GeoJsonExportService geoJsonExportService;
  @Mock
  private FileUploadService fileUploadService;
  @InjectMocks
  private ShapefileConversionService underTest;
  @Nested
  class WhenConvertingShapefileToGeoJson {

    @Mock
    private SupportingDocument shapefileZipDocument;
    @Mock
    private UploadedFile uploadedFile;
    @Mock
    private InputStream inputStream;
    private final String FILENAME = "FILENAME";
    private final ShapefileZipContents SHAPEFILE_ZIP_CONTENTS = new ShapefileZipContents("shapefile_name",
        List.of("filenames"), mock(SpatialReference.class), SupportedCoordinateReferenceSystem.ETRS89, List.of(mock(Geometry.class)));


    @BeforeEach
    void setup() {
      when(shapefileZipDocument.getUploadedFile()).thenReturn(uploadedFile);
      when(uploadedFile.getFilename()).thenReturn(FILENAME);
      when(fileUploadService.downloadFile(uploadedFile)).thenReturn(inputStream);
      when(shapefileZipService.getShapefileZipContents(inputStream, FILENAME)).thenReturn(SHAPEFILE_ZIP_CONTENTS);
    }

    @Test
    void verifyMethodCalls() {
      var result = underTest.convertShapefileToGeoJson(shapefileZipDocument);
      verify(geoJsonExportService).exportGeometryAsGeoJson(
          SHAPEFILE_ZIP_CONTENTS.thresholds(),
          SHAPEFILE_ZIP_CONTENTS.coordinateReferenceSystem(),
          SHAPEFILE_ZIP_CONTENTS.geometries());
    }
  }
}