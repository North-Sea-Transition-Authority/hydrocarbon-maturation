package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadResponse;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;

@ExtendWith(MockitoExtension.class)
class GeoJsonShapefileLinkServiceTest {

  @Mock
  private FileService fileService;
  @InjectMocks
  private GeoJsonShapefileLinkService underTest;
  @Mock
  private GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository;
  private User user = UserTestUtil.Builder().build();

  @Nested
  class WhenStoringGeoJson {
    private final String GEO_JSON = "GEO_JSON";
    private final String SHAPEFILE_NAME = "SHAPEFILE_NAME";
    @Mock
    private UploadedFile shapefile;
    @Mock
    private UploadedFile geoJsonUploadedFile;
    private final UUID FILE_ID = UUID.randomUUID();

    @Test
    void storeGeoJson_GeoJsonShapefileLinkIsPersisted() throws IOException {
      var fileUploadResponse = mock(FileUploadResponse.class);
      when(fileService.upload(any())).thenReturn(fileUploadResponse);
      when(fileUploadResponse.getFileId()).thenReturn(FILE_ID);
      when(fileService.find(FILE_ID)).thenReturn(Optional.of(geoJsonUploadedFile));

      var argumentCaptor = ArgumentCaptor.forClass(GeoJsonShapefileLink.class);
      underTest.storeGeoJson(shapefile, GEO_JSON, SHAPEFILE_NAME, user);

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