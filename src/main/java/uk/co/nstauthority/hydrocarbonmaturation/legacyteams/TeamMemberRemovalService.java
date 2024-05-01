package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore.OnshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;

@Service
public class TeamMemberRemovalService {

  public static final String LAST_ACCESS_MANAGER_ERROR_MESSAGE = "You cannot remove the last access manager of a team";
  public static final Set<? extends TeamRole> ACCESS_MANAGER_ROLES = Set.of(
      RegulatorTeamRole.ACCESS_MANAGER,
      IndustryTeamRole.ACCESS_MANAGER,
      NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM,
      SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_MANAGE_TEAM,
      OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM
  );

  private final TeamMemberService teamMemberService;

  private final TeamMemberRoleService teamMemberRoleService;

  @Autowired
  TeamMemberRemovalService(TeamMemberService teamMemberService, TeamMemberRoleService teamMemberRoleService) {
    this.teamMemberService = teamMemberService;
    this.teamMemberRoleService = teamMemberRoleService;
  }

  public void removeTeamMember(LegacyTeam legacyTeam, TeamMember teamMember) {
    if (canRemoveTeamMember(legacyTeam, teamMember)) {
      teamMemberRoleService.removeMemberFromTeam(legacyTeam, teamMember);
    } else {
      throw new IllegalStateException(
          "User [%s] cannot be removed from team [%s] as they are the last access manager"
              .formatted(teamMember.wuaId(), legacyTeam.getUuid())
      );
    }
  }

  public boolean canRemoveTeamMember(LegacyTeam legacyTeam, TeamMember teamMember) {
    var teamMembers = teamMemberService.getTeamMembers(legacyTeam);
    var accessManagers = filterAccessManagers(teamMembers);
    return accessManagers.stream()
        .anyMatch(tm -> !tm.wuaId().equals(teamMember.wuaId()));
  }

  private List<TeamMember> filterAccessManagers(Collection<TeamMember> teamMembers) {
    return teamMembers.stream()
        .filter(teamMember -> teamMember.roles().stream()
            .anyMatch(ACCESS_MANAGER_ROLES::contains))
        .toList();
  }

}
