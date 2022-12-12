package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DrillStemTestResultRepository extends CrudRepository<DrillStemTestResult, UUID> {

  List<DrillStemTestResult> findAllByDrillStemTest(DrillStemTest drillStemTest);
}
