package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.offshore;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

public enum OffshoreAreaTeamRole implements TeamRole {

  OFFSHORE_AREA_MANAGE_TEAM(
      "Access manager",
      "Can add, remove and update members of this team",
      10,
      EnumSet.of(RolePermission.GRANT_OFFSHORE_AREA_ROLES, RolePermission.VIEW_SYSTEM_OF_RECORD)
  ),
  OFFSHORE_AREA_TEAM_MEMBER(
      "Offshore area team member",
      "Manage discoveries and fields allocated to the offshore area team",
      20,
      EnumSet.of(RolePermission.VIEW_SYSTEM_OF_RECORD)
  );

  private final String screenDisplayText;
  private final String description;
  private final Integer displayOrder;
  private final Set<RolePermission> rolePermissions;

  OffshoreAreaTeamRole(String screenDisplayText, String description, Integer displayOrder,
                       Set<RolePermission> rolePermissions) {
    this.screenDisplayText = screenDisplayText;
    this.description = description;
    this.displayOrder = displayOrder;
    this.rolePermissions = rolePermissions;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getDisplayName() {
    return screenDisplayText;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public String getEnumName() {
    return name();
  }

  @Override
  public Set<RolePermission> getRolePermissions() {
    return rolePermissions;
  }

  static Optional<OffshoreAreaTeamRole> getRoleFromString(String role) {
    try {
      return Optional.of(OffshoreAreaTeamRole.valueOf(role.toUpperCase()));
    } catch (IllegalArgumentException exception) {
      return Optional.empty();
    }
  }
}
