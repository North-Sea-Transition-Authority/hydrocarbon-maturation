package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore;

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
public class NorthernOffshoreAreaTeamService {

  private final TeamService teamService;
  private final TeamMemberRoleService teamMemberRoleService;
  private final TeamMemberService teamMemberService;
  private final EnergyPortalUserService energyPortalUserService;

  @Autowired
  public NorthernOffshoreAreaTeamService(TeamService teamService,
                                         TeamMemberRoleService teamMemberRoleService, TeamMemberService teamMemberService,
                                         EnergyPortalUserService energyPortalUserService) {
    this.teamService = teamService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.teamMemberService = teamMemberService;
    this.energyPortalUserService = energyPortalUserService;
  }

  Optional<LegacyTeam> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.NORTHERN_OFFSHORE_AREA);
  }

  public void addUserTeamRoles(LegacyTeam legacyTeam, EnergyPortalUserDto userToAdd, Set<NorthernOffshoreAreaTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);

    teamMemberRoleService.addUserTeamRoles(legacyTeam, userToAdd, rolesAsStrings);
  }

  public boolean isNorthernOffshoreAreaUser(User user) {
    return getNorthernOffshoreAreaTeamForUser(user).isPresent();
  }

  public boolean isNorthernOffshoreTeamMember(User user) {
    return getNorthernOffshoreAreaTeamForUser(user)
        .map(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER.name())))
        .orElse(false);
  }

  public List<EnergyPortalUserDto> getNorthernOffShoreAreaTeamEnergyPortalUsers() {
    var northernOffshoreTeamMemberWuaIds = getNorthernOffshoreAreaTeamMembersWithRole(
        NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)
        .stream()
        .map(TeamMember::wuaId)
        .distinct()
        .toList();
    return energyPortalUserService.findByWuaIds(northernOffshoreTeamMemberWuaIds);
  }

  private Optional<LegacyTeam> getNorthernOffshoreAreaTeamForUser(User user) {
    return teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.NORTHERN_OFFSHORE_AREA)
        .stream()
        .findFirst();
  }

  private Set<String> getRolesAsStrings(Set<NorthernOffshoreAreaTeamRole> northernOffshoreAreaTeamRoles) {
    return northernOffshoreAreaTeamRoles.stream()
        .map(NorthernOffshoreAreaTeamRole::name)
        .collect(Collectors.toSet());
  }

  private List<TeamMember> getNorthernOffshoreAreaTeamMembersWithRole(
      NorthernOffshoreAreaTeamRole northernOffshoreAreaTeamRole) {
    return teamService.getAllTeamOfTypes(Set.of(TeamType.NORTHERN_OFFSHORE_AREA))
        .stream()
        .flatMap(team -> teamMemberService.getTeamMembers(team)
            .stream()
            .filter(teamMember -> teamMember.roles().contains(northernOffshoreAreaTeamRole)))
        .toList();
  }
}