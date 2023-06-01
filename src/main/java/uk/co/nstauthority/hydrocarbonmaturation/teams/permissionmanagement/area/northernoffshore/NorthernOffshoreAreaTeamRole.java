package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

public enum NorthernOffshoreAreaTeamRole implements TeamRole {

  NORTHERN_OFFSHORE_AREA_MANAGE_TEAM(
      "Access manager",
      "Can add, remove and update members of this team",
      10,
      EnumSet.of(RolePermission.GRANT_NORTHERN_OFFSHORE_AREA_ROLES, RolePermission.VIEW_SYSTEM_OF_RECORD)
  ),
  NORTHERN_OFFSHORE_AREA_TEAM_MEMBER(
      "Northern offshore area team member",
      "Manage discoveries and fields allocated to the northern offshore area team",
      20,
      EnumSet.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.VIEW_SYSTEM_OF_RECORD)
  );

  private final String screenDisplayText;
  private final String description;
  private final Integer displayOrder;
  private final Set<RolePermission> rolePermissions;

  NorthernOffshoreAreaTeamRole(String screenDisplayText, String description, Integer displayOrder,
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

  static Optional<NorthernOffshoreAreaTeamRole> getRoleFromString(String role) {
    try {
      return Optional.of(NorthernOffshoreAreaTeamRole.valueOf(role.toUpperCase()));
    } catch (IllegalArgumentException exception) {
      return Optional.empty();
    }
  }
}
