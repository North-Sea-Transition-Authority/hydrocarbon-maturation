package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
interface ResponsibleTeamRepository extends CrudRepository<ResponsibleTeam, UUID> {

  Optional<ResponsibleTeam> findResponsibleTeamByResource(Resource resource);
}
