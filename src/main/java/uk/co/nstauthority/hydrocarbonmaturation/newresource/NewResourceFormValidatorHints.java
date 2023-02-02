package uk.co.nstauthority.hydrocarbonmaturation.newresource;

import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

public class NewResourceFormValidatorHints {

  private boolean isRegulator;
  private ResourceType resourceType;

  public NewResourceFormValidatorHints(boolean isRegulator, ResourceType resourceType) {
    this.isRegulator = isRegulator;
    this.resourceType = resourceType;
  }

  public boolean isRegulator() {
    return isRegulator;
  }

  public void setRegulator(boolean regulator) {
    isRegulator = regulator;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  public void setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
  }
}

