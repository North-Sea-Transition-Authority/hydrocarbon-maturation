package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;

@Service
public class TeamService {

  private final TeamRepository teamRepository;
  private final TeamMemberRoleRepository teamMemberRoleRepository;

  @Autowired
  TeamService(TeamRepository teamRepository, TeamMemberRoleRepository teamMemberRoleRepository) {
    this.teamRepository = teamRepository;
    this.teamMemberRoleRepository = teamMemberRoleRepository;
  }

  public Optional<Team> getTeam(TeamId teamId, TeamType teamType) {
    return teamRepository.findByUuidAndTeamType(teamId.uuid(), teamType);
  }

  public Optional<Team> getTeam(TeamId teamId) {
    return teamRepository.findById(teamId.uuid());
  }

  public List<Team> getTeamsOfTypeThatUserBelongsTo(ServiceUserDetail user, TeamType teamType) {
    return teamRepository.findAllTeamsOfTypeThatUserIsMemberOf(user.wuaId(), teamType);
  }

  public List<Team> getTeamsThatUserBelongsTo(ServiceUserDetail user) {
    return teamMemberRoleRepository.findAllByWuaId(user.wuaId()).stream()
        .map(TeamMemberRole::getTeam)
        .distinct()
        .toList();
  }

  public boolean isUserInMultipleTeams(ServiceUserDetail user) {
    return getTeamsThatUserBelongsTo(user).size() > 1;
  }

  public void createTeam(Team team) {
    teamRepository.save(team);
  }

  public Optional<Team> getTeamByOrganisationGroupId(int organisationGroupId) {
    return teamRepository.findByOrganisationGroupId(organisationGroupId);
  }

  public List<Team> getAllTeams() {
    return teamRepository.findAll();
  }
}
