package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
interface RegulatorManagedInformationRepository extends CrudRepository<RegulatorManagedInformation, UUID> {

  Optional<RegulatorManagedInformation> findRegulatorManagedInformationByResource(Resource resource);
}
