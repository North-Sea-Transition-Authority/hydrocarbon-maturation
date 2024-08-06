package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@ExtendWith(MockitoExtension.class)
class FieldMapViewServiceTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private RestClient restClient;

  private FieldMapViewService fieldMapViewService;

  static final String FIELD_NAME = "Field name";
  static final String ONSHORE_URL = "https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/" +
      "ENG_Onshore_Fields_WGS84/FeatureServer/0/query?where=FIELDNAME%3D%27FIELD+NAME%27&f=geoJson&outFields=*";
  static final String OFFSHORE_URL = "https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/" +
      "UKCS_Offshore_Fields_WGS84/FeatureServer/0/query?where=FIELDNAME%3D%27FIELD+NAME%27&f=geoJson&outFields=*";

  @BeforeEach
  void setUp() {
    fieldMapViewService = new FieldMapViewService();
    ReflectionTestUtils.setField(fieldMapViewService, "restClient", restClient);
  }

  @Test
  void getFieldGeoJsonUrl_whenOnshore() {
    var field = FieldTestUtils.createField(FIELD_NAME, OnshoreOffshore.ONSHORE);
    when(restClient.get().uri(URI.create(ONSHORE_URL)).retrieve().body(GeoJsonResponse.class))
        .thenReturn(new GeoJsonResponse("type", List.of("some feature")));

    assertThat(fieldMapViewService.getFieldGeoJsonUrl(field))
        .isNotEmpty()
        .get()
        .extracting(
            FieldMapView::fieldGeoJsonUrl,
            FieldMapView::fieldName
        )
        .containsExactly(
            ONSHORE_URL,
            FIELD_NAME.toUpperCase()
        );
  }

  @Test
  void getFieldGeoJsonUrl_whenOffshore() {
    var field = FieldTestUtils.createField("   %s   ".formatted(FIELD_NAME), OnshoreOffshore.OFFSHORE);
    when(restClient.get().uri(URI.create(OFFSHORE_URL)).retrieve().body(GeoJsonResponse.class))
        .thenReturn(new GeoJsonResponse("type", List.of("some feature")));

    assertThat(fieldMapViewService.getFieldGeoJsonUrl(field))
        .isNotEmpty()
        .get()
        .extracting(
            FieldMapView::fieldGeoJsonUrl,
            FieldMapView::fieldName
        )
        .containsExactly(
            OFFSHORE_URL,
            FIELD_NAME.toUpperCase()
        );
  }

  @Test
  void getFieldGeoJsonUrl_whenNullResponse() {
    var field = FieldTestUtils.createField("   %s   ".formatted(FIELD_NAME), OnshoreOffshore.OFFSHORE);
    when(restClient.get().uri(URI.create(OFFSHORE_URL)).retrieve().body(GeoJsonResponse.class))
        .thenReturn(null);

    assertThat(fieldMapViewService.getFieldGeoJsonUrl(field))
        .isEmpty();
  }

  @Test
  void getFieldGeoJsonUrl_whenNoFeaturesReturned() {
    var field = FieldTestUtils.createField("   %s   ".formatted(FIELD_NAME), OnshoreOffshore.OFFSHORE);
    when(restClient.get().uri(URI.create(OFFSHORE_URL)).retrieve().body(GeoJsonResponse.class))
        .thenReturn(new GeoJsonResponse("type", List.of()));

    assertThat(fieldMapViewService.getFieldGeoJsonUrl(field))
        .isEmpty();
  }
}