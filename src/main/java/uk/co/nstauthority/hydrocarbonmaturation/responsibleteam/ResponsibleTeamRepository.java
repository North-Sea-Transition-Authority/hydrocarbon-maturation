package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ResponsibleTeamRepository extends CrudRepository<ResponsibleTeam, UUID> {
}
