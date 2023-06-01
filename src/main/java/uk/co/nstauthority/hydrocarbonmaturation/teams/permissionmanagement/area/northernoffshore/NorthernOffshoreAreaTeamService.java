package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore;

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
public class NorthernOffshoreAreaTeamService {

  private final TeamService teamService;
  private final TeamMemberRoleService teamMemberRoleService;
  private final TeamMemberService teamMemberService;

  @Autowired
  public NorthernOffshoreAreaTeamService(TeamService teamService,
                                 TeamMemberRoleService teamMemberRoleService, TeamMemberService teamMemberService) {
    this.teamService = teamService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.teamMemberService = teamMemberService;
  }

  Optional<Team> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.NORTHERN_OFFSHORE_AREA);
  }

  public void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<NorthernOffshoreAreaTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);

    teamMemberRoleService.addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  public boolean isNorthernOffshoreAreaUser(ServiceUserDetail user) {
    return getNorthernOffshoreAreaTeamForUser(user).isPresent();
  }

  public boolean isNorthernOffshoreTeamMember(ServiceUserDetail user) {
    return getNorthernOffshoreAreaTeamForUser(user)
        .map(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER.name())))
        .orElse(false);
  }

  private Optional<Team> getNorthernOffshoreAreaTeamForUser(ServiceUserDetail user) {
    return teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.NORTHERN_OFFSHORE_AREA)
        .stream()
        .findFirst();
  }

  private Set<String> getRolesAsStrings(Set<NorthernOffshoreAreaTeamRole> northernOffshoreAreaTeamRoles) {
    return northernOffshoreAreaTeamRoles.stream()
        .map(NorthernOffshoreAreaTeamRole::name)
        .collect(Collectors.toSet());
  }
}