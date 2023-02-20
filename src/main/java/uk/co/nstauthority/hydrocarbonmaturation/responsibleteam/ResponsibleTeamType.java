package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

public enum ResponsibleTeamType {
  EXPLORATION("Exploration", RolePermission.EXPLORATION),
  NORTHERN_NORTH_SEA("Northern North Sea"),
  CENTRAL_NORTH_SEA("Central North Sea"),
  SOUTHERN_NORTH_SEA("Southern North Sea"),
  WEST_OF_SHETLAND("West Of Shetland"),
  EAST_IRISH_SEA("East Irish Sea"),
  ONSHORE("Onshore");

  private final String displayName;
  private final Set<RolePermission> rolePermissions;


  ResponsibleTeamType(String displayName, RolePermission... rolePermissions) {
    this.displayName = displayName;
    this.rolePermissions = Arrays.stream(rolePermissions).collect(Collectors.toSet());
  }

  public String getDisplayName() {
    return displayName;
  }

  public Set<RolePermission> getRolePermissions() {
    return rolePermissions;
  }

  public static ResponsibleTeamType fromName(@Nullable String name) {
    if (name == null) {
      return null;
    }

    return valueOf(name);
  }
}
