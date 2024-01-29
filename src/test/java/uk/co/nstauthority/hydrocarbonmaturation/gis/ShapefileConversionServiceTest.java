package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

@ExtendWith(MockitoExtension.class)
class ShapefileConversionServiceTest {

  @Mock
  private ShapefileZipService shapefileZipService;
  @Mock
  private GeoJsonExportService geoJsonExportService;
  @Mock
  private FileService fileService;
  @InjectMocks
  private ShapefileConversionService underTest;
  @Nested
  class WhenConvertingShapefileToGeoJson {

    @Mock
    private UploadedFile shapefileZipDocument;
    @Mock
    private UploadedFile uploadedFile;

    private final InputStreamResource inputStream = new InputStreamResource(InputStream.nullInputStream());
    private final ShapefileZipContents SHAPEFILE_ZIP_CONTENTS = new ShapefileZipContents(
        "shapefile_name",
        List.of("filenames"),
        mock(SpatialReference.class),
        SupportedCoordinateReferenceSystem.ETRS89,
        List.of(mock(Geometry.class)),
        new ShapefileAttributes(List.of("headers"), List.of(List.of("rows")))
    );


    @BeforeEach
    void setup() {

      var FILENAME = "FILENAME";
      when(shapefileZipDocument.getName()).thenReturn(FILENAME);
      when(fileService.download(shapefileZipDocument)).thenReturn(ResponseEntity.ok().body(inputStream));
      when(shapefileZipService.getShapefileZipContents(any(), eq(FILENAME))).thenReturn(SHAPEFILE_ZIP_CONTENTS);
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