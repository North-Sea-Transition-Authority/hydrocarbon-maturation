package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;

@Service
public class PermissionService {

  private final TeamMemberService teamMemberService;

  @Autowired
  public PermissionService(TeamMemberService teamMemberService) {
    this.teamMemberService = teamMemberService;
  }

  public boolean hasPermission(User user, Set<RolePermission> requiredPermissions) {

    var teamMembers = teamMemberService.getUserAsTeamMembers(user);

    if (teamMembers == null) {
      return false;
    }

    Stream<Set<TeamRole>> setStream = teamMembers
        .stream()
        .map(TeamMember::roles);
    return isaBoolean(requiredPermissions, setStream);
  }

  private static boolean isaBoolean(Set<RolePermission> requiredPermissions, Stream<Set<TeamRole>> setStream) {
    return setStream
        .flatMap(Collection::stream)
        .map(TeamRole::getRolePermissions)
        .flatMap(Collection::stream)
        .anyMatch(requiredPermissions::contains);
  }
}
