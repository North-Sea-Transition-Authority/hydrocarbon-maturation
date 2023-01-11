package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;

@Service
public class TeamService {

  private final TeamRepository teamRepository;

  @Autowired
  TeamService(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public Optional<Team> getTeam(TeamId teamId, TeamType teamType) {
    return teamRepository.findByUuidAndTeamType(teamId.uuid(), teamType);
  }

  public List<Team> getTeamsOfTypeThatUserBelongsTo(ServiceUserDetail user, TeamType teamType) {
    return teamRepository.findAllTeamsOfTypeThatUserIsMemberOf(user.wuaId(), teamType);
  }

  public List<Team> getTeamsOfType(TeamType teamType) {
    return teamRepository.findTeamsByTeamType(teamType);
  }

  public void createTeam(Team team) {
    teamRepository.save(team);
  }

  public Optional<Team> getTeamByOrganisationGroupId(int organisationGroupId) {
    return teamRepository.findByOrganisationGroupId(organisationGroupId);
  }
}
