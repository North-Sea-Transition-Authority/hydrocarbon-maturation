package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class SubmissionGeoJsonService {
  private final SubmissionGeoJsonRepository submissionGeojsonRepository;

  @Autowired
  SubmissionGeoJsonService(SubmissionGeoJsonRepository submissionGeoJsonRepository) {
    this.submissionGeojsonRepository = submissionGeoJsonRepository;
  }

  public Optional<SubmissionGeoJson> getShapefileGeoJsonForSubmissionDetail(SubmissionDetail submissionDetail) {
    return submissionGeojsonRepository.findBySubmissionDetail(submissionDetail);
  }

  @Transactional
  public void storeGeoJson(String geoJson, SubmissionDetail submissionDetail) {
    var shapefileGeojson = new SubmissionGeoJson();
    shapefileGeojson.setSubmissionDetail(submissionDetail);
    shapefileGeojson.setGeojson(geoJson);
    submissionGeojsonRepository.save(shapefileGeojson);
  }

  @Transactional
  public void deleteGeojson(SubmissionDetail submissionDetail) {
    submissionGeojsonRepository.findBySubmissionDetail(submissionDetail).ifPresent(
        submissionGeojsonRepository::delete
    );
  }
}
