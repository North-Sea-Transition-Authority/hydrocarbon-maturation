package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceGroupRepository extends JpaRepository<ResourceGroup, UUID> {
}
