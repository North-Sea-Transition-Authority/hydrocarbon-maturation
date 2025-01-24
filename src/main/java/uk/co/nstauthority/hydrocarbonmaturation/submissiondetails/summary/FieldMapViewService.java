package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
public class FieldMapViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FieldMapViewService.class);

  static final String URL_TEMPLATE = "https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/" +
      "{onshoreOffshore}/FeatureServer/0/query?where=FIELDNAME%3D%27{fieldName}%27&f=geoJson&outFields=*";
  static final String OFFSHORE_PATH_VARIABLE = "UKCS_Offshore_Fields_WGS84";
  static final String ONSHORE_PATH_VARIABLE = "ENG_Onshore_Fields_WGS84";


  private final RestClient restClient;

  public FieldMapViewService() {
    this.restClient = RestClient.builder()
        .requestFactory(new SimpleClientHttpRequestFactory())
        .build();
  }

  public Optional<FieldMapView> getFieldGeoJsonUrl(Field field) {
    var isOnshore = OnshoreOffshore.ONSHORE.equals(field.getOnshoreOffshore());
    var onshoreOffshore = isOnshore
        ? ONSHORE_PATH_VARIABLE
        : OFFSHORE_PATH_VARIABLE;

    var fieldName = field.getCommonNameForField().trim().toUpperCase();
    var encodedFieldName = URLEncoder.encode(fieldName, Charset.defaultCharset());

    var url = UriComponentsBuilder.fromUriString(URL_TEMPLATE)
        .buildAndExpand(onshoreOffshore, encodedFieldName)
        .toUriString();

    if (!returnsValidShapeMap(url)) {
      return Optional.empty();
    }

    return Optional.of(new FieldMapView(url, fieldName));
  }

  private boolean returnsValidShapeMap(String url) {
    try {
      GeoJsonResponse response = restClient
          .get()
          .uri(URI.create(url))
          .retrieve()
          .body(GeoJsonResponse.class);

      if (response == null) {
        return false;
      }

      return !response.features().isEmpty();
    } catch (Exception e) {
      LOGGER.warn("Exception thrown when performing shapefile request for URL: %s.".formatted(url), e);
      return false;
    }
  }
}