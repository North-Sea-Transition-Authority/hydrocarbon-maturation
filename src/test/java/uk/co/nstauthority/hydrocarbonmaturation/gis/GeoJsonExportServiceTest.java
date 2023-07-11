package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.esri.core.geometry.GeoJsonExportFlags;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorExportToGeoJson;
import com.esri.core.geometry.OperatorUnion;
import com.esri.core.geometry.SimpleGeometryCursor;
import com.esri.core.geometry.SpatialReference;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeoJsonExportServiceTest {
  @InjectMocks
  private GeoJsonExportService underTest;
  @Mock
  private OperatorExportToGeoJson exportToGeoJson;
  @Mock
  private OperatorUnion operatorUnion;

  @Nested
  class WhenExportingShapefileAsGeoJson {
    @Mock
    private Geometry geometry1;
    @Mock
    private Geometry geometry2;
    @Mock
    private SpatialReference spatialReference;
    private final SupportedCoordinateReferenceSystem coordinateReferenceSystem = SupportedCoordinateReferenceSystem.WGS84;

    @Test
    void noGeometriesFound_ThrowException() {

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> underTest.exportGeometryAsGeoJson(spatialReference, coordinateReferenceSystem, List.of()));
    }

    @Test
    void oneGeometryFound_VerifySingleGeometryPassedInForConversion() {
      var geometryCursor = new SimpleGeometryCursor(geometry1);

      var result = underTest.exportGeometryAsGeoJson(
          spatialReference, coordinateReferenceSystem, List.of(geometry1)
      );

      verify(operatorUnion, never()).execute(eq(geometryCursor), any(), any());
      verify(exportToGeoJson).execute(GeoJsonExportFlags.geoJsonExportDefaults, coordinateReferenceSystem.toSpatialReference(), geometry1);
    }

    @Test
    void twoGeometriesFound_VerifyGeometryUnionedAndPassedInForConversion() {
      var simpleGeometryCursor = Mockito.mock(SimpleGeometryCursor.class);
      when(operatorUnion.execute(any(SimpleGeometryCursor.class), eq(spatialReference), eq(null))).thenReturn(simpleGeometryCursor);

      var geometry = Mockito.mock(Geometry.class);
      when(simpleGeometryCursor.next()).thenReturn(geometry);

      var result = underTest.exportGeometryAsGeoJson(spatialReference, coordinateReferenceSystem, List.of(geometry1, geometry2));

      verify(exportToGeoJson, never()).execute(geometry1);
      verify(exportToGeoJson, never()).execute(geometry2);
      verify(exportToGeoJson).execute(GeoJsonExportFlags.geoJsonExportDefaults, coordinateReferenceSystem.toSpatialReference(), geometry);
    }
  }

  @Nested
  class WhenExportingGeometryAsGeoJson {
    @Mock
    private Geometry geometry;
    private final SupportedCoordinateReferenceSystem supportedCoordinateReferenceSystem = SupportedCoordinateReferenceSystem.WGS84;

    @Test
    void verifyGeometryPassedInForConversion() {
      var result = underTest.exportGeometryAsGeoJson(supportedCoordinateReferenceSystem, geometry);

      verify(exportToGeoJson).execute(GeoJsonExportFlags.geoJsonExportDefaults, supportedCoordinateReferenceSystem.toSpatialReference(), geometry);
    }

  }

}