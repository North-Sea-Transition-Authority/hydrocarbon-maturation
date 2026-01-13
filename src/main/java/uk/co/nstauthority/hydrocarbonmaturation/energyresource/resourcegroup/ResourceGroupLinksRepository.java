package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
public interface ResourceGroupLinksRepository extends CrudRepository<ResourceGroupLink, UUID> {

  ResourceGroupLink findByResource(Resource resource);

  List<ResourceGroupLink> findByResourceGroup(ResourceGroup resourceGroupId);

  List<ResourceGroupLink> findAllByResourceIn(List<Resource> resources);
}