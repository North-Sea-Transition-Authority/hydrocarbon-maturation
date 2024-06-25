package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJson;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJsonRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJsonService;

@ExtendWith(MockitoExtension.class)
class SubmissionGeoJsonServiceTest {
  @Mock
  private SubmissionGeoJsonRepository submissionGeojsonRepository;
  @InjectMocks
  private SubmissionGeoJsonService underTest;
  private static final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Captor
  private ArgumentCaptor<SubmissionGeoJson> shapefileGeojsonArgumentCaptor;

  @Nested
  class WhenStoringShapefileAsGeoJson {

    @Test
    void convertsShapefileToGeoJson() {
      var geoJson = "GEO_JSON";

      underTest.storeGeoJson(geoJson, SUBMISSION_DETAIL);

      verify(submissionGeojsonRepository).save(shapefileGeojsonArgumentCaptor.capture());
      var shapefileGeojson = shapefileGeojsonArgumentCaptor.getValue();
      assertThat(shapefileGeojson)
          .extracting(
              SubmissionGeoJson::getSubmissionDetail,
              SubmissionGeoJson::getGeojson
          )
          .containsExactly(
              SUBMISSION_DETAIL,
              geoJson
          );
    }
  }

  @Nested
  class WhenDeletingGeojson {
    @Test
    void geoJsonShapefileLinkFound_DeleteFile() {
      var shapefileGeojson = new SubmissionGeoJson();
      shapefileGeojson.setSubmissionDetail(SUBMISSION_DETAIL);
      when(submissionGeojsonRepository.findBySubmissionDetail(SUBMISSION_DETAIL)).thenReturn(Optional.of(shapefileGeojson));

      underTest.deleteGeojson(SUBMISSION_DETAIL);

      verify(submissionGeojsonRepository).delete(shapefileGeojson);
    }
  }
}