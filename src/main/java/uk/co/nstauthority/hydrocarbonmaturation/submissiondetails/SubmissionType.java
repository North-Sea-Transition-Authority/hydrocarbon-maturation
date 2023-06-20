package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum SubmissionType implements Displayable {
  CREATE_PROSPECT("Prospect create", "Create a new prospect", Set.of(ResourceType.PROSPECT), 10),
  PROSPECT_UPDATE("Prospect update", "Update an existing prospect", Set.of(ResourceType.PROSPECT), 20),
  POST_DRILL_DRY_HOLE("Post-drill dry hole submission", "Advise of a post-drill dry hole", Set.of(ResourceType.PROSPECT), 30),
  CREATE_DISCOVERY_FROM_PROSPECT("Post-drill discovery", "Advise of a post-drill discovery",
      Set.of(ResourceType.PROSPECT, ResourceType.DISCOVERY), 40),
  CREATE_DISCOVERY("Post-drill discovery", "Create a discovery not related to a drill on a prospect",
      Set.of(ResourceType.DISCOVERY), 50),
  UPDATE_DISCOVERY("Post-drill discovery update", "Update an existing discovery", Set.of(ResourceType.DISCOVERY), 60),
  RELINQUISHMENT_PROSPECT("Relinquishment", "Advise of a relinquishment", Set.of(ResourceType.PROSPECT), 70),
  RELINQUISHMENT_DISCOVERY("Relinquishment", "Advise of a relinquishment", Set.of(ResourceType.DISCOVERY), 80);

  private final String typeName;
  private final String displayName;
  private final Set<ResourceType> resourceTypes;
  private final int displayOrder;

  SubmissionType(String typeName, String displayName, Set<ResourceType> resourceTypes,
                 int displayOrder) {
    this.typeName = typeName;
    this.displayName = displayName;
    this.resourceTypes = resourceTypes;
    this.displayOrder = displayOrder;
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
}
