package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

class GeneralDetailsForm {

  private String prospectName;

  private OnshoreOffshore onshoreOffshore;

  private Boolean prospectLocatedInUkcs;

  private String percentageLocatedInUkcs;

  private InformationSource informationSource;

  private String otherSourceOfInformation;

  public String getProspectName() {
    return prospectName;
  }

  public void setProspectName(String prospectName) {
    this.prospectName = prospectName;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }

  public Boolean getProspectLocatedInUkcs() {
    return prospectLocatedInUkcs;
  }

  public void setProspectLocatedInUkcs(Boolean prospectLocatedInUkcs) {
    this.prospectLocatedInUkcs = prospectLocatedInUkcs;
  }

  public String getPercentageLocatedInUkcs() {
    return percentageLocatedInUkcs;
  }

  public void setPercentageLocatedInUkcs(String percentageLocatedInUkcs) {
    this.percentageLocatedInUkcs = percentageLocatedInUkcs;
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
