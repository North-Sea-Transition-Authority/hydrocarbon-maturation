package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
public class OnshoreAreaTeamService {

  private final TeamService teamService;
  private final TeamMemberRoleService teamMemberRoleService;
  private final TeamMemberService teamMemberService;

  @Autowired
  public OnshoreAreaTeamService(TeamService teamService,
                                TeamMemberRoleService teamMemberRoleService, TeamMemberService teamMemberService) {
    this.teamService = teamService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.teamMemberService = teamMemberService;
  }

  Optional<Team> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.ONSHORE_AREA);
  }

  public void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<OnshoreAreaTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);

    teamMemberRoleService.addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  public boolean isOnshoreAreaUser(ServiceUserDetail user) {
    return getOnshoreAreaTeamForUser(user).isPresent();
  }

  public boolean isOnshoreTeamMember(ServiceUserDetail user) {
    return getOnshoreAreaTeamForUser(user)
        .map(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER.name())))
        .orElse(false);
  }

  private Optional<Team> getOnshoreAreaTeamForUser(ServiceUserDetail user) {
    return teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.ONSHORE_AREA)
        .stream()
        .findFirst();
  }

  private Set<String> getRolesAsStrings(Set<OnshoreAreaTeamRole> onshoreAreaTeamRoles) {
    return onshoreAreaTeamRoles.stream()
        .map(OnshoreAreaTeamRole::name)
        .collect(Collectors.toSet());
  }
}
