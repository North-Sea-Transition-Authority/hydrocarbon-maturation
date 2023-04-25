package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShapefileZipServiceTest {


  @InjectMocks
  private ShapefileZipService underTest;

  @Nested
  class WhenGettingShapefileName {

    @Test
    void hasShapefile() {
      var files = List.of("file1.shp", "file2.txt");

      var result = underTest.getShapefileName(files);

      assertThat(result).isEqualTo("file1");
    }

    @Test
    void doesNotHaveShapefile() {
      var files = List.of("file1.shx", "file2.txt");

      var result = underTest.getShapefileName(files);

      assertThat(result).isNull();
    }

    @Test
    void hasMultipleShapefiles() {
      var files = List.of("file1.shp", "file2.shp");

      var result = underTest.getShapefileName(files);

      assertThat(result).isNull();
    }
  }

  @Nested
  class WhenCheckingIfHasRequiredFiles {

    @Test
    void hasShapefiles() {
      var files = List.of("file1.shp", "file1.shx", "file1.prj", "file1.dbf");

      var result = underTest.hasRequiredFiles(files);

      assertThat(result).isTrue();
    }

    @Test
    void missingShx() {
      var files = List.of("file1.shp", "file1.prj", "file1.dbf");

      var hasRequiredFiles = underTest.hasRequiredFiles(files);

      assertThat(hasRequiredFiles).isFalse();
    }

    @Test
    void missingShp() {
      var files = List.of("file1.shx", "file1.prj", "file1.dbf");

      var result = underTest.hasRequiredFiles(files);

      assertThat(result).isFalse();
    }

    @Test
    void missingPrj() {
      var files = List.of("file1.shp", "file1.shx", "file1.dbf");

      var result = underTest.hasRequiredFiles(files);

      assertThat(result).isFalse();
    }

    @Test
    void missingDbf() {
      var files = List.of("file1.shp", "file1.shx", "file1.prj");

      var result = underTest.hasRequiredFiles(files);

      assertThat(result).isFalse();
    }
  }
}