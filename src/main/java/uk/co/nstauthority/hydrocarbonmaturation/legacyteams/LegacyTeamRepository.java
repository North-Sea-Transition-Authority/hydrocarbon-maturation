package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface LegacyTeamRepository extends CrudRepository<LegacyTeam, UUID> {

  Optional<LegacyTeam> findByUuidAndTeamType(UUID uuid, TeamType teamType);

  @Query(
      """
      SELECT DISTINCT tmr.team
      FROM TeamMemberRole tmr
      WHERE tmr.wuaId = :wuaId
      AND tmr.team.teamType = :teamType
      """
  )
  List<LegacyTeam> findAllTeamsOfTypeThatUserIsMemberOf(Long wuaId, TeamType teamType);

  Optional<LegacyTeam> findByOrganisationGroupId(int organisationGroupId);

  List<LegacyTeam> findAll();

  List<LegacyTeam> findAllByTeamType(TeamType teamType);
}
