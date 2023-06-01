package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
public class SouthernOffshoreAreaTeamService {

  private final TeamService teamService;
  private final TeamMemberRoleService teamMemberRoleService;
  private final TeamMemberService teamMemberService;
  private final EnergyPortalUserService energyPortalUserService;

  @Autowired
  public SouthernOffshoreAreaTeamService(TeamService teamService, TeamMemberRoleService teamMemberRoleService,
                                         TeamMemberService teamMemberService,
                                         EnergyPortalUserService energyPortalUserService) {
    this.teamService = teamService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.teamMemberService = teamMemberService;
    this.energyPortalUserService = energyPortalUserService;
  }

  Optional<Team> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.SOUTHERN_OFFSHORE_AREA);
  }

  public void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<SouthernOffshoreAreaTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);

    teamMemberRoleService.addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  public boolean isSouthernOffshoreAreaUser(ServiceUserDetail user) {
    return getSouthernOffshoreAreaTeamForUser(user).isPresent();
  }

  public boolean isSouthernOffshoreTeamMember(ServiceUserDetail user) {
    return getSouthernOffshoreAreaTeamForUser(user)
        .map(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            Set.of(SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER.name())))
        .orElse(false);
  }

  public List<EnergyPortalUserDto> getSouthernOffShoreAreaTeamEnergyPortalUsers() {
    var southernOffshoreTeamMemberWuaIds = getSouthernOffshoreAreaTeamMembersWithRole(
        SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER)
        .stream()
        .map(TeamMember::wuaId)
        .distinct()
        .toList();
    return energyPortalUserService.findByWuaIds(southernOffshoreTeamMemberWuaIds);
  }

  private Optional<Team> getSouthernOffshoreAreaTeamForUser(ServiceUserDetail user) {
    return teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.SOUTHERN_OFFSHORE_AREA)
        .stream()
        .findFirst();
  }

  private Set<String> getRolesAsStrings(Set<SouthernOffshoreAreaTeamRole> southernOffshoreAreaTeamRoles) {
    return southernOffshoreAreaTeamRoles.stream()
        .map(SouthernOffshoreAreaTeamRole::name)
        .collect(Collectors.toSet());
  }

  private List<TeamMember> getSouthernOffshoreAreaTeamMembersWithRole(
      SouthernOffshoreAreaTeamRole southernOffshoreAreaTeamRole) {
    return teamService.getAllTeamOfTypes(Set.of(TeamType.SOUTHERN_OFFSHORE_AREA))
        .stream()
        .flatMap(team -> teamMemberService.getTeamMembers(team)
            .stream()
            .filter(teamMember -> teamMember.roles().contains(southernOffshoreAreaTeamRole)))
        .toList();
  }
}
