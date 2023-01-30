package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

public enum ResponsibleTeamType {
  EXPLORATION(Set.of(RolePermission.EXPLORATION)),
  NORTHERN_NORTH_SEA(Set.of()),
  CENTRAL_NORTH_SEA(Set.of()),
  SOUTHERN_NORTH_SEA(Set.of()),
  WEST_OF_SHETLAND(Set.of()),
  EAST_IRISH_SEA(Set.of()),
  ONSHORE(Set.of());

  private final Set<RolePermission> rolePermissions;


  ResponsibleTeamType(Set<RolePermission> rolePermissions) {
    this.rolePermissions = rolePermissions;
  }

  public Set<RolePermission> getRolePermissions() {
    return rolePermissions;
  }
}
