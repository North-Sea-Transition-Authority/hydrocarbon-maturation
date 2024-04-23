package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.FileUploadRequest;
import uk.co.fivium.fileuploadlibrary.core.FileUsage;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadResponse;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.MultipartFileInputStream;

@ExtendWith(MockitoExtension.class)
class GeoJsonShapefileLinkServiceTest {
  @Mock
  private FileService fileService;
  @Mock
  private GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository;
  @Mock
  private ShapefileConversionService shapefileConversionService;
  @InjectMocks
  private GeoJsonShapefileLinkService underTest;
  private final User USER = UserTestUtil.Builder().build();

  @Nested
  class WhenStoringShapefileAsGeoJson {
    @Mock
    private UploadedFile uploadedFile;
    @Mock
    private ShapefileZipContents shapefileZip;
    @Mock
    private UploadedFile geoJsonUploadedFile;
    private final UUID FILE_ID = UUID.randomUUID();
    @Test
    void convertsShapefileToGeoJson() throws IOException {
      var spy = spy(underTest);

      var geoJson = "GEO_JSON";
      when(shapefileConversionService.getGeoJsonFromShapefileZip(shapefileZip)).thenReturn(geoJson);

      var fileUploadResponse = mock(FileUploadResponse.class);
      when(fileService.upload(any())).thenReturn(fileUploadResponse);
      when(fileUploadResponse.getFileId()).thenReturn(FILE_ID);
      when(fileService.find(FILE_ID)).thenReturn(Optional.of(geoJsonUploadedFile));

      var result = spy.storeShapefileAsGeoJson(uploadedFile, shapefileZip);

      verify(spy).storeGeoJson(uploadedFile, geoJson, null);
      assertThat(result)
          .extracting(
              GeoJsonShapefileLink::getShapefile,
              GeoJsonShapefileLink::getGeoJsonFile
          )
          .containsExactly(
              uploadedFile,
              geoJsonUploadedFile
          );
    }
  }
  @Nested
  class WhenStoringGeoJson {
    private final String GEO_JSON = "GEO_JSON";
    @Mock
    private UploadedFile SHAPEFILE;
    @Mock
    private UploadedFile GEO_JSON_UPLOADED_FILE;
    private final UUID FILE_ID = UUID.randomUUID();

    @Mock
    private User user;
    @Mock
    private UploadedFile shapefile;

    @Test
    void geoJsonShapefileLinkIsPersisted() throws IOException {
      var fileUploadResponse = mock(FileUploadResponse.class);
      when(fileService.upload(any())).thenReturn(fileUploadResponse);
      when(fileUploadResponse.getFileId()).thenReturn(FILE_ID);
      when(fileService.find(FILE_ID)).thenReturn(Optional.of(GEO_JSON_UPLOADED_FILE));

      var result = underTest.storeGeoJson(SHAPEFILE, GEO_JSON, USER);

      verify(geoJsonShapefileLinkRepository).save(result);
      verify(fileService).updateUsage(eq(GEO_JSON_UPLOADED_FILE), any());
      assertThat(result)
          .extracting(
              GeoJsonShapefileLink::getShapefile,
              GeoJsonShapefileLink::getGeoJsonFile
          )
          .containsExactly(
              SHAPEFILE,
              GEO_JSON_UPLOADED_FILE
          );
    }

    @Test
    void geoJsonShapefileLinkAlreadyExists() throws IOException {
      var geoJsonShapefileLink = mock(GeoJsonShapefileLink.class);
      when(geoJsonShapefileLinkRepository.findByShapefile(SHAPEFILE))
          .thenReturn(Optional.of(geoJsonShapefileLink));

      var result = underTest.storeGeoJson(SHAPEFILE, GEO_JSON, USER);

      assertThat(result).isEqualTo(geoJsonShapefileLink);
    }
  }

  @Nested
  class WhenDeletingGeojson {
    @Mock
    private UploadedFile shapefile;
    @Mock
    private UploadedFile geoJsonUploadedFile;

    @Test
    void geoJsonShapefileLinkFound_DeleteFile() {
      var geoJsonShapefileLink = new GeoJsonShapefileLink();
      geoJsonShapefileLink.setGeoJsonFile(geoJsonUploadedFile);
      when(geoJsonShapefileLinkRepository.findByShapefile(shapefile)).thenReturn(Optional.of(geoJsonShapefileLink));

      underTest.deleteGeojson(shapefile);

      verify(fileService).delete(geoJsonUploadedFile);
      verify(geoJsonShapefileLinkRepository).deleteByShapefile(shapefile);
    }

    @Test
    void geoJsonShapefileLinkNotFound() {
      when(geoJsonShapefileLinkRepository.findByShapefile(shapefile)).thenReturn(Optional.empty());

      underTest.deleteGeojson(shapefile);

      verify(fileService, never()).delete(any());
      verify(geoJsonShapefileLinkRepository).deleteByShapefile(shapefile);
    }
  }

  @Nested
  class WhenGettingBuilderFileUploadRequestFunction {
    @Mock
    private MultipartFileInputStream multipartFileInputStream;
    private final FileUploadRequest.Builder VALID_BUILDER = FileUploadRequest.newBuilder()
        .withBucket("bucket")
        .withMaximumSize(DataSize.ofBytes(1))
        .withFileExtensions(Set.of("txt", "zip"));

    @Test
    void withUser() {
      var fileUploadRequest = underTest.getBuilderFileUploadRequestFunction(multipartFileInputStream, USER).apply(VALID_BUILDER);

      assertThat(fileUploadRequest)
          .extracting(
              FileUploadRequest::uploadedBy,
              FileUploadRequest::multipartFile
          )
          .containsExactly(
              USER.wuaId().toString(),
              multipartFileInputStream
          );
    }

    @Test
    void withNullUser() {
      var fileUploadRequest = underTest.getBuilderFileUploadRequestFunction(multipartFileInputStream, null).apply(VALID_BUILDER);

      assertThat(fileUploadRequest)
          .extracting(
              FileUploadRequest::uploadedBy,
              FileUploadRequest::multipartFile
          )
          .containsExactly(
              null,
              multipartFileInputStream
          );
    }
  }

  @Nested
  class WhenGettingBuilderFileUsageFunction {

    private static final FileDocumentType FILE_DOCUMENT_TYPE = FileDocumentType.SHAPEFILE_GEOJSON;
    private final FileUsage.Builder BUILDER = FileUsage.newBuilder();

    @Test
    void returnsFileUsageWithCorrectProperties() {
      var shapefile = new UploadedFile();
      shapefile.setUsageId("123");
      shapefile.setUsageType("test");

      var fileUsage = underTest.getBuilderFileUsageFunction(shapefile).apply(BUILDER);

      assertThat(fileUsage).extracting(
          FileUsage::usageId,
          FileUsage::usageType,
          FileUsage::documentType
      ).containsExactly(
          "123",
          "test",
          FILE_DOCUMENT_TYPE.name()
      );
    }
  }
}