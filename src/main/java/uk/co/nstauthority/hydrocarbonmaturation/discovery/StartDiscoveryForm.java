package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class StartDiscoveryForm implements SubmissionForm {

  private String prospectSelectedOption;

  public String getProspectSelectedOption() {
    return prospectSelectedOption;
  }

  public void setProspectSelectedOption(String prospectSelectedOption) {
    this.prospectSelectedOption = prospectSelectedOption;
  }
}
