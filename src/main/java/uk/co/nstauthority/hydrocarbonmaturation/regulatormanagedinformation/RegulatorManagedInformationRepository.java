package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
public interface RegulatorManagedInformationRepository extends CrudRepository<RegulatorManagedInformation, UUID> {

  Optional<RegulatorManagedInformation> findRegulatorManagedInformationByResource(Resource resource);

  @EntityGraph(attributePaths = {"resource"})
  List<RegulatorManagedInformation> findAllByResourceNameContainingIgnoreCase(String resourceName);
}
