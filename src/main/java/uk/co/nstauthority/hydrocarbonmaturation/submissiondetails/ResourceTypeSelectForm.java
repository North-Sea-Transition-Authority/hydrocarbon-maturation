package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

public class ResourceTypeSelectForm {

  private ResourceType resourceType;

  public ResourceType getResourceType() {
    return resourceType;
  }

  public ResourceTypeSelectForm setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
    return this;
  }
}
