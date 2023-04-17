package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class GeneralDetailsForm implements SubmissionForm {

  private Boolean resourceLocatedInUkcs;
  private DecimalInput percentageLocatedInUkcs;

  private List<String> quadrantBlocks = new ArrayList<>();
  private List<String> licences = new ArrayList<>();

  private String quadrantBlockSelect;
  private String licenceSelect;
  private InformationSource informationSource;
  private String licenceApplication;
  private String otherSourceOfInformation;
  private Boolean resourceOnALicence;
  private String relinquishmentDay;
  private String relinquishmentMonth;
  private String relinquishmentYear;

  public Boolean getResourceLocatedInUkcs() {
    return resourceLocatedInUkcs;
  }

  public void setResourceLocatedInUkcs(Boolean resourceLocatedInUkcs) {
    this.resourceLocatedInUkcs = resourceLocatedInUkcs;
  }

  public DecimalInput getPercentageLocatedInUkcs() {
    return percentageLocatedInUkcs;
  }

  public void setPercentageLocatedInUkcs(DecimalInput percentageLocatedInUkcs) {
    this.percentageLocatedInUkcs = percentageLocatedInUkcs;
  }

  public List<String> getQuadrantBlocks() {
    return quadrantBlocks;
  }

  public void setQuadrantBlocks(List<String> quadrantBlocks) {
    this.quadrantBlocks = quadrantBlocks;
  }

  public String getQuadrantBlockSelect() {
    return quadrantBlockSelect;
  }

  public void setQuadrantBlockSelect(String quadrantBlockSelect) {
    this.quadrantBlockSelect = quadrantBlockSelect;
  }

  public InformationSource getInformationSource() {
    return informationSource;
  }

  public void setInformationSource(
      InformationSource informationSource) {
    this.informationSource = informationSource;
  }

  public String getLicenceApplication() {
    return licenceApplication;
  }

  public void setLicenceApplication(String licenceApplication) {
    this.licenceApplication = licenceApplication;
  }

  public String getOtherSourceOfInformation() {
    return otherSourceOfInformation;
  }

  public void setOtherSourceOfInformation(String otherSourceOfInformation) {
    this.otherSourceOfInformation = otherSourceOfInformation;
  }

  public List<String> getLicences() {
    return licences;
  }

  public void setLicences(
      List<String> licences) {
    this.licences = licences;
  }

  public Boolean getResourceOnALicence() {
    return resourceOnALicence;
  }

  public void setResourceOnALicence(Boolean resourceOnALicence) {
    this.resourceOnALicence = resourceOnALicence;
  }

  public String getLicenceSelect() {
    return licenceSelect;
  }

  public void setLicenceSelect(String licenceSelect) {
    this.licenceSelect = licenceSelect;
  }

  public String getRelinquishmentDay() {
    return relinquishmentDay;
  }

  public void setRelinquishmentDay(String relinquishmentDay) {
    this.relinquishmentDay = relinquishmentDay;
  }

  public String getRelinquishmentMonth() {
    return relinquishmentMonth;
  }

  public void setRelinquishmentMonth(String relinquishmentMonth) {
    this.relinquishmentMonth = relinquishmentMonth;
  }

  public String getRelinquishmentYear() {
    return relinquishmentYear;
  }

  public void setRelinquishmentYear(String relinquishmentYear) {
    this.relinquishmentYear = relinquishmentYear;
  }
}
