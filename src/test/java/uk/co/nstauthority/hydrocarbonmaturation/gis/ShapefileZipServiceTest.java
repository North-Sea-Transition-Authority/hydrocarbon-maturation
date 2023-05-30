package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShapefileZipServiceTest {
  @InjectMocks
  private ShapefileZipService underTest;
  @Mock
  private ShapefileImportService shapefileImportService;
  @Mock
  private SupportedCoordinateReferenceSystemService supportedCoordinateReferenceSystemService;

  @Nested
  class WhenCheckingIfHasRequiredFiles {

    private final String STRING = "STRING";

    @Test
    void hasShapefiles() {

      var files = List.of("file1.shp", "file1.shx", "file1.prj", "file1.dbf");
      var shapefileZipContents = new ShapefileZipContents("shapefile_name",
          files, null, null, null, null);

      var result = underTest.hasRequiredFiles(shapefileZipContents);

      assertThat(result).isTrue();
    }

    @Test
    void missingShx() {
      var files = List.of("file1.shp", "file1.prj", "file1.dbf");
      var shapefileZipContents = new ShapefileZipContents("shapefile_name",
          files, null, null, null, null);

      var hasRequiredFiles = underTest.hasRequiredFiles(shapefileZipContents);

      assertThat(hasRequiredFiles).isFalse();
    }

    @Test
    void missingShp() {
      var files = List.of("file1.shx", "file1.prj", "file1.dbf");
      var shapefileZipContents = new ShapefileZipContents("shapefile_name",
          files, null, null, null, null);

      var result = underTest.hasRequiredFiles(shapefileZipContents);

      assertThat(result).isFalse();
    }

    @Test
    void missingPrj() {
      var files = List.of("file1.shp", "file1.shx", "file1.dbf");
      var shapefileZipContents = new ShapefileZipContents("shapefile_name",
          files, null, null, null, null);

      var result = underTest.hasRequiredFiles(shapefileZipContents);

      assertThat(result).isFalse();
    }

    @Test
    void missingDbf() {
      var files = List.of("file1.shp", "file1.shx", "file1.prj");
      var shapefileZipContents = new ShapefileZipContents("shapefile_name",
          files, null, null, null, null);

      var result = underTest.hasRequiredFiles(shapefileZipContents);

      assertThat(result).isFalse();
    }
  }
}