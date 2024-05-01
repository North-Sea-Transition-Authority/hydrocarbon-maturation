package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;

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

  Optional<LegacyTeam> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.SOUTHERN_OFFSHORE_AREA);
  }

  public void addUserTeamRoles(LegacyTeam legacyTeam, EnergyPortalUserDto userToAdd, Set<SouthernOffshoreAreaTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);

    teamMemberRoleService.addUserTeamRoles(legacyTeam, userToAdd, rolesAsStrings);
  }

  public boolean isSouthernOffshoreAreaUser(User user) {
    return getSouthernOffshoreAreaTeamForUser(user).isPresent();
  }

  public boolean isSouthernOffshoreTeamMember(User user) {
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

  private Optional<LegacyTeam> getSouthernOffshoreAreaTeamForUser(User user) {
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
