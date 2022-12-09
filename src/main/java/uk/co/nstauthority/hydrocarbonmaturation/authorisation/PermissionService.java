package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.util.Collection;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

@Service
public class PermissionService {

  private final TeamMemberService teamMemberService;

  @Autowired
  public PermissionService(TeamMemberService teamMemberService) {
    this.teamMemberService = teamMemberService;
  }

  public boolean hasPermission(ServiceUserDetail user, Set<RolePermission> requiredPermissions) {

    var teamMembers = teamMemberService.getUserAsTeamMembers(user);

    if (teamMembers == null) {
      return false;
    }

    return teamMembers
        .stream()
        .map(TeamMember::roles)
        .flatMap(Collection::stream)
        .map(TeamRole::getRolePermissions)
        .flatMap(Collection::stream)
        .anyMatch(requiredPermissions::contains);
  }
}
