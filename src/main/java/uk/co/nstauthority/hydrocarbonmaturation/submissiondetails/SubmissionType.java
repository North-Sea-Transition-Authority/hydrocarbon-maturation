package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

public enum SubmissionType implements Displayable {
  CREATE_PROSPECT(
      "Prospect create",
      "Create a new prospect",
      Set.of(ResourceType.PROSPECT),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT),
      null,
      10
  ),
  PROSPECT_UPDATE(
      "Prospect update",
      "Update an existing prospect",
      Set.of(ResourceType.PROSPECT),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT),
      null,
      20
  ),
  POST_DRILL_DRY_HOLE(
      "Post-drill dry hole submission",
      "Advise of a post-drill dry hole",
      Set.of(ResourceType.PROSPECT),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT),
      null,
      30
  ),
  CREATE_DISCOVERY_FROM_PROSPECT(
      "Post-drill discovery",
      "Advise of a post-drill discovery",
      Set.of(ResourceType.PROSPECT, ResourceType.DISCOVERY),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT),
      null,
      40
  ),
  CREATE_DISCOVERY(
      "Post-drill discovery",
      "Create a discovery not related to a drill on a prospect",
      Set.of(ResourceType.DISCOVERY),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT),
      null,
      50
  ),
  UPDATE_DISCOVERY(
      "Post-drill discovery update",
      "Update an existing discovery",
      Set.of(ResourceType.DISCOVERY),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT, RolePermission.ONSHORE_AREA_TEAM_MEMBER,
          RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER),
      null,
      60
  ),
  RELINQUISHMENT_PROSPECT(
      "Relinquishment",
      "Advise of a relinquishment",
      Set.of(ResourceType.PROSPECT),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT),
      null,
      70
  ),
  RELINQUISHMENT_DISCOVERY(
      "Relinquishment",
      "Advise of a relinquishment",
      Set.of(ResourceType.DISCOVERY),
      Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT, RolePermission.ONSHORE_AREA_TEAM_MEMBER,
          RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER),
      null,
      80
  ),
  CREATE_FIELD(
      "Field",
      "Create a new field",
      Set.of(ResourceType.FIELD),
      Set.of(RolePermission.ONSHORE_AREA_TEAM_MEMBER,
          RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER),
      null,
      90
  ),
  CREATE_CSR(
      "Concept Select Report (CSR)",
      "Provide a concept select report (CSR)",
      Set.of(ResourceType.FIELD),
      Set.of(RolePermission.INDUSTRY_SUBMIT, RolePermission.ONSHORE_AREA_TEAM_MEMBER,
          RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER),
      "Only the final report agreed with NSTA should be provided",
      100
  );

  private final String typeName;
  private final String displayName;
  private final Set<ResourceType> resourceTypes;
  private final Set<RolePermission> permissions;
  private final String hintText;
  private final int displayOrder;

  SubmissionType(String typeName, String displayName, Set<ResourceType> resourceTypes, Set<RolePermission> permissions,
                 String hintText, int displayOrder) {
    this.typeName = typeName;
    this.displayName = displayName;
    this.resourceTypes = resourceTypes;
    this.permissions = permissions;
    this.hintText = hintText;
    this.displayOrder = displayOrder;
  }

  static List<SubmissionType> getSubmissionTypesForResourceType(ResourceType resourceType) {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> submissionType.resourceTypes.contains(resourceType))
        .sorted(Comparator.comparingInt(Displayable::getDisplayOrder))
        .toList();
  }

  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }

  public Set<ResourceType> getResourceTypes() {
    return resourceTypes;
  }

  public Set<RolePermission> getPermissions() {
    return permissions;
  }

  public String getHintText() {
    return hintText;
  }
}
