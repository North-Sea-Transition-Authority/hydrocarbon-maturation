package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class PostDrillDryHoleForm implements SubmissionForm {

  private String prospectSelectedOption;

  public String getProspectSelectedOption() {
    return prospectSelectedOption;
  }

  public void setProspectSelectedOption(String prospectSelectedOption) {
    this.prospectSelectedOption = prospectSelectedOption;
  }
}
