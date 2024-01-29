package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupportedCoordinateReferenceSystemServiceTest {

  @InjectMocks
  private SupportedCoordinateReferenceSystemService underTest;

  @Nested
  class WhenGettingSupportedCoordinateReferenceSystemFromWkt {
    private final String GEOCS_WELL_KNOWN_TEXT = ShapefileTestUtils.getStreamAsString(ShapefileTestUtils.SHAPEFILE_PROJECT_RESOURCE_LOCATION);
    private final String PROJCS_WELL_KNOWN_TEXT = ShapefileTestUtils.getStreamAsString(ShapefileTestUtils.PROJCS_SHAPEFILE_PROJECT_RESOURCE_LOCATION);

    WhenGettingSupportedCoordinateReferenceSystemFromWkt() throws IOException {
    }

    @Test
    void CanHandleGeoCs() {
      var crs = underTest.getSupportedCoordinateReferenceSystemFromWkt(GEOCS_WELL_KNOWN_TEXT);
      assertThat(crs).isEqualTo(SupportedCoordinateReferenceSystem.ED50);
    }

    @Test
    void CanHandleProjCs() {
      var crs = underTest.getSupportedCoordinateReferenceSystemFromWkt(PROJCS_WELL_KNOWN_TEXT);
      assertThat(crs).isEqualTo(SupportedCoordinateReferenceSystem.OSGB36_NG);
    }
  }
}