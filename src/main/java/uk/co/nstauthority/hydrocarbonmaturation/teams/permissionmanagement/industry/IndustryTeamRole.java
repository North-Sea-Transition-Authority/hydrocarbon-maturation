package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

public enum IndustryTeamRole implements TeamRole {

  ACCESS_MANAGER(
      "Access manager",
      "Can add, remove and update members of this team",
      10,
      EnumSet.of(RolePermission.INDUSTRY_MANAGE_ORGANISATION, RolePermission.VIEW_SYSTEM_OF_RECORD)
  ),
  SUBMITTER(
      "Submitter",
      "Submit updates to a prospect, discovery and fields",
      20,
      EnumSet.of(RolePermission.INDUSTRY_SUBMIT, RolePermission.VIEW_SYSTEM_OF_RECORD)
  ),
  VIEWER(
      "Viewer",
      "View all prospects, discoveries and fields data. Other roles automatically get this permission",
      30,
      EnumSet.of(RolePermission.VIEW_SYSTEM_OF_RECORD)
  )
  ;

  private final String screenDisplayText;
  private final String description;
  private final Integer displayOrder;
  private final Set<RolePermission> rolePermissions;

  IndustryTeamRole(String screenDisplayText, String description, Integer displayOrder,
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

  static Optional<IndustryTeamRole> getRoleFromString(String role) {
    try {
      return Optional.of(IndustryTeamRole.valueOf(role.toUpperCase()));
    } catch (IllegalArgumentException exception) {
      return Optional.empty();
    }
  }
}
