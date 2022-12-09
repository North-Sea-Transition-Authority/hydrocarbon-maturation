package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

public enum RegulatorTeamRole implements TeamRole {

  ACCESS_MANAGER(
      "Access manager",
      "Manage regulator access to the team",
      10,
      EnumSet.of(RolePermission.GRANT_ROLES, RolePermission.VIEW_SYSTEM_OF_RECORD)
  ),
  INDUSTRY_ACCESS_MANAGER(
      "Industry access manager",
      "Manage industry access to the service",
      20,
      EnumSet.of(RolePermission.MANAGE_ORGANISATIONS, RolePermission.VIEW_SYSTEM_OF_RECORD)
  ),
  EXPLORATION(
      "Exploration team member",
      "Manage prospects and discoveries with the Exploration team",
      30,
      EnumSet.of(RolePermission.EXPLORATION, RolePermission.VIEW_SYSTEM_OF_RECORD)
  ),
  VIEWER(
      "Viewer",
      "View all prospect, discovery and field data. Other roles automatically get this permission",
      30,
      EnumSet.of(RolePermission.VIEW_SYSTEM_OF_RECORD)
  );

  private final String screenDisplayText;
  private final String description;
  private final Integer displayOrder;
  private final Set<RolePermission> rolePermissions;

  RegulatorTeamRole(String screenDisplayText,
                    String description,
                    Integer displayOrder,
                    Set<RolePermission> rolePermissions) {
    this.screenDisplayText = screenDisplayText;
    this.description = description;
    this.displayOrder = displayOrder;
    this.rolePermissions = rolePermissions;
  }

  @Override
  public String getDisplayName() {
    return screenDisplayText;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public Set<RolePermission> getRolePermissions() {
    return rolePermissions;
  }

  static Optional<RegulatorTeamRole> getRoleFromString(String role) {
    try {
      return Optional.of(RegulatorTeamRole.valueOf(role.toUpperCase()));
    } catch (IllegalArgumentException exception) {
      return Optional.empty();
    }
  }


}
