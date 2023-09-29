package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Repository
interface DrillStemTestRepository extends CrudRepository<DrillStemTest, UUID> {

  Optional<DrillStemTest> findByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);
}
