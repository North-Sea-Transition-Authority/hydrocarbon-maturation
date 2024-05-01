package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TeamMemberRoleRepository extends CrudRepository<TeamMemberRole, UUID> {

  List<TeamMemberRole> findAllByTeam(LegacyTeam legacyTeam);

  boolean existsByWuaId(long wuaId);

  boolean existsByWuaIdAndTeam_Uuid(long wuaId, UUID teamId);

  boolean existsByWuaIdAndTeam_UuidAndRoleIn(long wuaId, UUID teamId, Set<String> roles);

  List<TeamMemberRole> findAllByTeamAndWuaId(LegacyTeam legacyTeam, Long wuaId);

  void deleteAllByTeamAndWuaId(LegacyTeam legacyTeam, Long wuaId);

  List<TeamMemberRole> findAllByWuaId(long wuaId);

  List<TeamMemberRole> findAllByTeamInAndRoleIn(List<LegacyTeam> legacyTeams, Set<String> roles);
}
