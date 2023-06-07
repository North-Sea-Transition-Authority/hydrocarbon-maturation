package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator;

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
public class RegulatorTeamService {

  private final TeamService teamService;

  private final TeamMemberService teamMemberService;

  private final TeamMemberRoleService teamMemberRoleService;

  private final EnergyPortalUserService energyPortalUserService;

  @Autowired
  RegulatorTeamService(TeamService teamService,
                       TeamMemberService teamMemberService,
                       TeamMemberRoleService teamMemberRoleService,
                       EnergyPortalUserService energyPortalUserService) {
    this.teamService = teamService;
    this.teamMemberService = teamMemberService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.energyPortalUserService = energyPortalUserService;
  }

  public Optional<Team> getRegulatorTeamForUser(ServiceUserDetail user) {
    return teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR)
        .stream()
        .findFirst();
  }

  public List<TeamMember> getRegulatorTeamMembersWithRole(RegulatorTeamRole regulatorTeamRole) {
    return teamService.getRegulatorTeams()
        .stream()
        .flatMap(team -> teamMemberService.getTeamMembers(team)
            .stream()
            .filter(teamMember -> teamMember.roles().contains(regulatorTeamRole)))
        .toList();
  }

  public boolean isRegulatorUser(ServiceUserDetail user) {
    return getRegulatorTeamForUser(user).isPresent();
  }

  public boolean isRegulatorTeamRole(ServiceUserDetail user, RegulatorTeamRole regulatorTeamRole) {
    return getRegulatorTeamForUser(user)
        .map(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            Set.of(regulatorTeamRole.name())))
        .orElse(false);
  }

  public boolean isExploration(ServiceUserDetail userDetail) {
    return isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION);
  }

  public List<EnergyPortalUserDto> getExplorationTeamEnergyPortalUsers() {
    var explorationTeamMemberWuaIds = getRegulatorTeamMembersWithRole(RegulatorTeamRole.EXPLORATION)
        .stream()
        .map(TeamMember::wuaId)
        .distinct()
        .toList();
    return energyPortalUserService.findByWuaIds(explorationTeamMemberWuaIds);
  }

  Optional<Team> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.REGULATOR);
  }

  void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<RegulatorTeamRole> roles) {
    var rolesAsStrings = roles
        .stream()
        .map(RegulatorTeamRole::name)
        .collect(Collectors.toSet());

    teamMemberRoleService.addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }
}
