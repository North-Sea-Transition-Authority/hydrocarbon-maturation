package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

public class RelinquishmentForm {

  private ResourceType chosenResourceType;
  private String prospectSelectedOption;
  private String discoverySelectedOption;

  public ResourceType getChosenResourceType() {
    return chosenResourceType;
  }

  public void setChosenResourceType(ResourceType chosenResourceType) {
    this.chosenResourceType = chosenResourceType;
  }

  public String getProspectSelectedOption() {
    return prospectSelectedOption;
  }

  public void setProspectSelectedOption(String prospectSelectedOption) {
    this.prospectSelectedOption = prospectSelectedOption;
  }

  public String getDiscoverySelectedOption() {
    return discoverySelectedOption;
  }

  public void setDiscoverySelectedOption(String discoverySelectedOption) {
    this.discoverySelectedOption = discoverySelectedOption;
  }
}
