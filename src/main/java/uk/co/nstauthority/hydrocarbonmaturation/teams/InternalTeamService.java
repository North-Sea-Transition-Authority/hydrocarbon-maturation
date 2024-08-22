package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;

@Service
public class InternalTeamService {

  private final TeamQueryService teamQueryService;

  @Autowired
  public InternalTeamService(TeamQueryService teamQueryService) {
    this.teamQueryService = teamQueryService;
  }

  /**
   * Checks if the logged in user is in the regulator team or any area team.
   * @return a boolean indicating if the user is in the regulator team or any area team.
   */
  public boolean isRegulatorOrAreaTeamUser(User user) {
    var userTeams = teamQueryService.getTeamRolesForUser(user.wuaId())
            .stream()
            .map(TeamRole::getTeam)
            .toList();
    var expectedTeamTypes = Set.of(
            TeamType.REGULATOR,
            TeamType.NORTHERN_OFFSHORE_AREA,
            TeamType.SOUTHERN_OFFSHORE_AREA,
            TeamType.ONSHORE_AREA);
    return userTeams.stream().map(Team::getTeamType).anyMatch(expectedTeamTypes::contains);
  }
}
