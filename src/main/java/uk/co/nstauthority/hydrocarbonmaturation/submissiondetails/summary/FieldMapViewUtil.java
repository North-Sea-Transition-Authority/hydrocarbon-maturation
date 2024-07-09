package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.web.util.UriComponentsBuilder;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

public class FieldMapViewUtil {

  static final String URL_TEMPLATE = "https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/" +
      "{onshoreOffshore}/FeatureServer/0/query?where=FIELDNAME%3D%27{fieldName}%27&f=geoJson&outFields=*";
  static final String OFFSHORE_PATH_VARIABLE = "UKCS_Offshore_Fields_WGS84";
  static final String ONSHORE_PATH_VARIABLE = "ENG_Onshore_Fields_WGS84";

  private FieldMapViewUtil() {
    throw new IllegalStateException("FieldMapViewUtil");
  }

  public static FieldMapView getFieldGeoJsonUrl(Field field) {
    var isOnshore = OnshoreOffshore.ONSHORE.equals(field.getOnshoreOffshore());
    var onshoreOffshore = isOnshore
        ? ONSHORE_PATH_VARIABLE
        : OFFSHORE_PATH_VARIABLE;

    var fieldName = field.getCommonNameForField().trim();

    var url = UriComponentsBuilder.fromPath(URL_TEMPLATE)
        .buildAndExpand(onshoreOffshore, fieldName.toUpperCase())
        .toString();

    return new FieldMapView(url, fieldName);
  }
}