package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

public enum SubmissionType implements Displayable {
  CREATE_PROSPECT(
      "New prospect",
      "Create a new prospect (or lead)",
      Set.of(ResourceType.PROSPECT),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT),
      null,
      10
  ),
  PROSPECT_UPDATE(
      "Prospect update",
      "Update an existing prospect (or lead)",
      Set.of(ResourceType.PROSPECT),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT),
      null,
      20
  ),
  POST_DRILL_DRY_HOLE(
      "Post-drill dry hole submission",
      "Advise of a post-drill dry hole",
      Set.of(ResourceType.PROSPECT),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT),
      null,
      30
  ),
  CREATE_DISCOVERY_FROM_PROSPECT(
      "Post-drill discovery",
      "Advise of a post-drill discovery",
      Set.of(ResourceType.PROSPECT, ResourceType.DISCOVERY),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT),
      null,
      40
  ),
  CREATE_DISCOVERY(
      "Post-drill discovery",
      "Create a discovery not related to a drill on a prospect",
      Set.of(ResourceType.DISCOVERY),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT),
      null,
      50
  ),
  UPDATE_DISCOVERY(
      "Post-drill discovery update",
      "Update an existing discovery",
      Set.of(ResourceType.DISCOVERY),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      null,
      60
  ),
  RELINQUISHMENT_PROSPECT(
      "Prospect relinquishment",
      "Advise of a prospect relinquishment",
      Set.of(ResourceType.PROSPECT),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT),
      null,
      70
  ),
  RELINQUISHMENT_DISCOVERY(
      "Discovery relinquishment",
      "Advise of a discovery relinquishment",
      Set.of(ResourceType.DISCOVERY),
      Set.of(Role.EXPLORATION, Role.INDUSTRY_SUBMIT, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      null,
      80
  ),
  CREATE_FIELD(
      "Field",
      "Create a new field",
      Set.of(ResourceType.FIELD),
      Set.of(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      null,
      90
  ),
  CREATE_CSR(
      "Concept Select Report",
      "Provide a concept select report (CSR)",
      "CSR",
      Set.of(ResourceType.FIELD),
      Set.of(Role.INDUSTRY_SUBMIT, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      "Only the final report agreed with NSTA should be provided",
      100
  ),
  CREATE_FDP(
      "Field Development Plan",
      "Provide a field development plan (FDP)",
      "FDP",
      Set.of(ResourceType.FIELD),
      Set.of(Role.INDUSTRY_SUBMIT, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      "Only the final plan agreed with NSTA should be provided",
      110
  ),
  CREATE_FDPA(
      "Field Development Plan Addendum",
      "Provide a field development plan addendum (FDPA)",
      "FDPA",
      Set.of(ResourceType.FIELD),
      Set.of(Role.INDUSTRY_SUBMIT, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      "Only the final plan agreed with NSTA should be provided",
      120
  ),
  CREATE_COP(
      "Cessation of Production notification",
      "Advise of cessation of production (CoP)",
      "CoP",
      Set.of(ResourceType.FIELD),
      Set.of(Role.INDUSTRY_SUBMIT, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      "Only provide once production has ceased permanently and all the wells have been shut in",
      130
  );

  private final String typeName;
  private final String displayName;
  private final String acronym;
  private final Set<ResourceType> resourceTypes;
  private final Set<Role> roles;
  private final String hintText;
  private final int displayOrder;

  SubmissionType(String typeName,
                 String displayName,
                 String acronym,
                 Set<ResourceType> resourceTypes,
                 Set<Role> roles,
                 String hintText,
                 int displayOrder) {
    this.typeName = typeName;
    this.displayName = displayName;
    this.acronym = acronym;
    this.resourceTypes = resourceTypes;
    this.roles = roles;
    this.hintText = hintText;
    this.displayOrder = displayOrder;
  }

  SubmissionType(String typeName,
                 String displayName,
                 Set<ResourceType> resourceTypes,
                 Set<Role> roles,
                 String hintText,
                 int displayOrder) {
    this.typeName = typeName;
    this.displayName = displayName;
    this.acronym = null;
    this.resourceTypes = resourceTypes;
    this.roles = roles;
    this.hintText = hintText;
    this.displayOrder = displayOrder;
  }

  public static List<SubmissionType> getSubmissionTypesForResourceType(ResourceType resourceType) {
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

  public String getAcronym() {
    return acronym;
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

  public Set<Role> getRoles() {
    return roles;
  }

  public String getHintText() {
    return hintText;
  }

  public static boolean isUpdateType(SubmissionType submissionType) {
    return switch (submissionType) {
      case  PROSPECT_UPDATE, UPDATE_DISCOVERY -> true;
      default -> false;
    };
  }

  public static Set<Role> getRolesThatCanSubmit() {
    return Arrays.stream(SubmissionType.values())
        .map(SubmissionType::getRoles)
        .flatMap(Collection::stream).collect(Collectors.toSet());
  }

  public static Set<ResourceType> getResourceTypesForRoles(Set<Role> roles) {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> submissionType.getRoles().stream().anyMatch(roles::contains))
        .map(SubmissionType::getResourceTypes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }
}
