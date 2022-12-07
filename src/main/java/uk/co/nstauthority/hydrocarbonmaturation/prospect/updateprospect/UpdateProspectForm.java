package uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;

public class UpdateProspectForm implements SubmissionForm {

  private String prospectSelectedOption;

  private InformationSource informationSource;

  private String otherSourceOfInformation;

  private String licenceApplication;

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

  public String getLicenceApplication() {
    return licenceApplication;
  }

  public void setLicenceApplication(String licenceApplication) {
    this.licenceApplication = licenceApplication;
  }
}
