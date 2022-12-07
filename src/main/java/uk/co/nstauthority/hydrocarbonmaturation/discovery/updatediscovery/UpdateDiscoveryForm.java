package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class UpdateDiscoveryForm implements SubmissionForm {

  private String discoverySelectedOption;

  public String getDiscoverySelectedOption() {
    return discoverySelectedOption;
  }

  public void setDiscoverySelectedOption(String discoverySelectedOption) {
    this.discoverySelectedOption = discoverySelectedOption;
  }
}
