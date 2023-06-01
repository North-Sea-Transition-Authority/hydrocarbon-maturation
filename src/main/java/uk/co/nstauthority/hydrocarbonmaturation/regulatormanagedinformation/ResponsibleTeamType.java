package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

public enum ResponsibleTeamType implements Displayable {
  EXPLORATION("Exploration team", RolePermission.EXPLORATION),
  NORTHERN_NORTH_SEA_WEST_OF_SHETLAND("Northern North Sea and West of Shetland team",
      RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER),
  CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA("Central North Sea, Southern North Sea and East Irish Sea team",
      RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER),
  ONSHORE("Onshore team", RolePermission.ONSHORE_AREA_TEAM_MEMBER);

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
