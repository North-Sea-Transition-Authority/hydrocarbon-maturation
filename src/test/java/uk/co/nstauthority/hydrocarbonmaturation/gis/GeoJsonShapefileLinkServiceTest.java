package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder.SHAPEFILE_GEOJSON;

import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;

@ExtendWith(MockitoExtension.class)
class GeoJsonShapefileLinkServiceTest {

  @Mock
  private FileUploadService fileUploadService;
  @InjectMocks
  private GeoJsonShapefileLinkService underTest;
  @Mock
  private GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository;

  @Nested
  class WhenStoringGeoJson {
    private final String GEO_JSON = "GEO_JSON";
    private final String SHAPEFILE_NAME = "SHAPEFILE_NAME";
    @Mock
    private SupportingDocument shapefile;
    @Mock
    private UploadedFile geoJsonUploadedFile;

    @BeforeEach
    void setUp() {
      when(fileUploadService.createUploadedFile(
          SHAPEFILE_GEOJSON,
          GEO_JSON.length(),
          SHAPEFILE_NAME,
          GeoJsonShapefileLinkService.GEOJSON_CONTENT_TYPE
      )).thenReturn(geoJsonUploadedFile);
    }

    @Test
    void geoJsonIsUploaded() {
      underTest.storeGeoJson(shapefile, GEO_JSON, SHAPEFILE_NAME);

      verify(fileUploadService).uploadFile(any(InputStream.class), eq(geoJsonUploadedFile));
    }

    @Test
    void geoJsonShapefileLinkIsPersisted() {
      var argumentCaptor = ArgumentCaptor.forClass(GeoJsonShapefileLink.class);
      underTest.storeGeoJson(shapefile, GEO_JSON, SHAPEFILE_NAME);

      verify(geoJsonShapefileLinkRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue())
          .extracting(
              GeoJsonShapefileLink::getShapefile,
              GeoJsonShapefileLink::getGeoJsonFile
          )
          .containsExactly(
              shapefile,
              geoJsonUploadedFile
          );
    }
  }
}