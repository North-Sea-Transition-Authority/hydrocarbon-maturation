package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;

public class UpdateProspectForm implements SubmissionForm {

  private String prospectSelectedOption;

  private InformationSource informationSource;

  private String otherSourceOfInformation;

  public UpdateProspectForm() {
  }

  public UpdateProspectForm(String prospectSelectedOption, InformationSource informationSource,
                            String otherSourceOfInformation) {
    this.prospectSelectedOption = prospectSelectedOption;
    this.informationSource = informationSource;
    this.otherSourceOfInformation = otherSourceOfInformation;
  }

  public String getProspectSelectedOption() {
    return prospectSelectedOption;
  }

  public void setProspectSelectedOption(String prospectSelectedOption) {
    this.prospectSelectedOption = prospectSelectedOption;
  }

  public InformationSource getInformationSource() {
    return informationSource;
  }

  public void setInformationSource(
      InformationSource informationSource) {
    this.informationSource = informationSource;
  }

  public String getOtherSourceOfInformation() {
    return otherSourceOfInformation;
  }

  public void setOtherSourceOfInformation(String otherSourceOfInformation) {
    this.otherSourceOfInformation = otherSourceOfInformation;
  }
}
