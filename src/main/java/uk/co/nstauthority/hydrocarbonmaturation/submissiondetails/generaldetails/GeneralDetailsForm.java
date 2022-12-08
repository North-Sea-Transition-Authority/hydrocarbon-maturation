package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class GeneralDetailsForm implements SubmissionForm {

  private String resourceName;
  //TODO HM-129
  private OnshoreOffshore onshoreOffshore;
  private Boolean resourceLocatedInUkcs;
  private DecimalInput percentageLocatedInUkcs;
  //TODO HM-129

  private List<String> quadrantBlocks = new ArrayList<>();
  private List<String> licences = new ArrayList<>();

  private String quadrantBlockSelect;
  private String licenceSelect;
  private InformationSource informationSource;
  private String licenceApplication;
  private String responsibleOrganisationId;
  private String otherSourceOfInformation;
  private Boolean resourceOnALicence;

  public String getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(String responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }

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
}
