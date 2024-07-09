package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

class FieldMapViewUtilTest {

  static final String FIELD_NAME = "Davy";
  static final String ONSHORE_URL = "https:/data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/" +
      "ENG_Onshore_Fields_WGS84/FeatureServer/0/query?where=FIELDNAME%3D%27DAVY%27&f=geoJson&outFields=*";
  static final String OFFSHORE_URL = "https:/data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/" +
      "UKCS_Offshore_Fields_WGS84/FeatureServer/0/query?where=FIELDNAME%3D%27DAVY%27&f=geoJson&outFields=*";

  @Test
  void getFieldGeoJsonUrl_whenOnshore() {
    var field = FieldTestUtils.createField(FIELD_NAME, OnshoreOffshore.ONSHORE);

    assertThat(FieldMapViewUtil.getFieldGeoJsonUrl(field))
        .extracting(
            FieldMapView::fieldGeoJsonUrl,
            FieldMapView::fieldName
        )
        .containsExactly(
            ONSHORE_URL,
            FIELD_NAME
        );
  }

  @Test
  void getFieldGeoJsonUrl_whenOffshore() {
    var field = FieldTestUtils.createField("   %s   ".formatted(FIELD_NAME), OnshoreOffshore.OFFSHORE);

    assertThat(FieldMapViewUtil.getFieldGeoJsonUrl(field))
        .extracting(
            FieldMapView::fieldGeoJsonUrl,
            FieldMapView::fieldName
        )
        .containsExactly(
            OFFSHORE_URL,
            FIELD_NAME
        );
  }
}