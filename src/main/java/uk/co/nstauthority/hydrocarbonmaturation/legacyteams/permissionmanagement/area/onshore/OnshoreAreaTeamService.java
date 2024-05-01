package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore;

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
public class OnshoreAreaTeamService {

  private final TeamService teamService;
  private final TeamMemberRoleService teamMemberRoleService;
  private final TeamMemberService teamMemberService;
  private final EnergyPortalUserService energyPortalUserService;

  @Autowired
  public OnshoreAreaTeamService(TeamService teamService,
                                TeamMemberRoleService teamMemberRoleService, TeamMemberService teamMemberService,
                                EnergyPortalUserService energyPortalUserService) {
    this.teamService = teamService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.teamMemberService = teamMemberService;
    this.energyPortalUserService = energyPortalUserService;
  }

  Optional<LegacyTeam> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.ONSHORE_AREA);
  }

  public void addUserTeamRoles(LegacyTeam legacyTeam, EnergyPortalUserDto userToAdd, Set<OnshoreAreaTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);

    teamMemberRoleService.addUserTeamRoles(legacyTeam, userToAdd, rolesAsStrings);
  }

  public boolean isOnshoreAreaUser(User user) {
    return getOnshoreAreaTeamForUser(user).isPresent();
  }

  public boolean isOnshoreTeamMember(User user) {
    return getOnshoreAreaTeamForUser(user)
        .map(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER.name())))
        .orElse(false);
  }

  public List<EnergyPortalUserDto> getOnShoreAreaTeamEnergyPortalUsers() {
    var onshoreTeamMemberWuaIds = getOnshoreAreaTeamMembersWithRole(
        OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER)
        .stream()
        .map(TeamMember::wuaId)
        .distinct()
        .toList();
    return energyPortalUserService.findByWuaIds(onshoreTeamMemberWuaIds);
  }

  private Optional<LegacyTeam> getOnshoreAreaTeamForUser(User user) {
    return teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.ONSHORE_AREA)
        .stream()
        .findFirst();
  }

  private Set<String> getRolesAsStrings(Set<OnshoreAreaTeamRole> onshoreAreaTeamRoles) {
    return onshoreAreaTeamRoles.stream()
        .map(OnshoreAreaTeamRole::name)
        .collect(Collectors.toSet());
  }

  private List<TeamMember> getOnshoreAreaTeamMembersWithRole(
      OnshoreAreaTeamRole onshoreAreaTeamRole) {
    return teamService.getAllTeamOfTypes(Set.of(TeamType.ONSHORE_AREA))
        .stream()
        .flatMap(team -> teamMemberService.getTeamMembers(team)
            .stream()
            .filter(teamMember -> teamMember.roles().contains(onshoreAreaTeamRole)))
        .toList();
  }
}
