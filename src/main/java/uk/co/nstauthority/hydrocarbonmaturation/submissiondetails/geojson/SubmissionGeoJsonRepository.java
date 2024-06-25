package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface SubmissionGeoJsonRepository extends CrudRepository<SubmissionGeoJson, UUID> {

  Optional<SubmissionGeoJson> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
