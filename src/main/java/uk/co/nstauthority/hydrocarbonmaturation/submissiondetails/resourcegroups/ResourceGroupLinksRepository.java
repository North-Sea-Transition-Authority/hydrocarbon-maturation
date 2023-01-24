package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
interface ResourceGroupLinksRepository extends CrudRepository<ResourceGroupLink, UUID> {

  Optional<ResourceGroupLink> findByResource(Resource resource);

  Optional<ResourceGroupLink> findByResourceGroupIdAndPreferredViewIsTrue(UUID resourceGroupId);
}