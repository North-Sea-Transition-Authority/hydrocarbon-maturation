package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

public class RelinquishmentForm {

  private ResourceType chosenResourceType;
  private String resourceSelectedOption;

  public ResourceType getChosenResourceType() {
    return chosenResourceType;
  }

  public void setChosenResourceType(ResourceType chosenResourceType) {
    this.chosenResourceType = chosenResourceType;
  }

  public String getResourceSelectedOption() {
    return resourceSelectedOption;
  }

  public void setResourceSelectedOption(String resourceSelectedOption) {
    this.resourceSelectedOption = resourceSelectedOption;
  }
}
