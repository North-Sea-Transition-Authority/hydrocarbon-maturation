package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface LicenceRepository extends CrudRepository<Licence, UUID> {

  void deleteAllByGeneralDetail(GeneralDetail generalDetail);

  List<Licence> findAllByGeneralDetail(GeneralDetail generalDetail);

}
