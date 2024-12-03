package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.ArrayList;
import java.util.List;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class GeneralDetailsForm implements SubmissionForm {

  private Boolean prospectDiscoveryLocatedInUkcs;
  private Boolean prospectDiscoveryLocatedInEngland;
  private DecimalInput percentageLocatedInUkcs;
  private DecimalInput percentageLocatedInEngland;

  private List<String> quadrantBlocks = new ArrayList<>();
  private List<String> licences = new ArrayList<>();

  private String quadrantBlockSelect;
  private String licenceSelect;
  private InformationSource informationSource;
  private String licenceApplication;
  private String otherSourceOfInformation;
  private String relinquishmentDay;
  private String relinquishmentMonth;
  private String relinquishmentYear;

  private String fieldStoppedProducingDay;
  private String fieldStoppedProducingMonth;
  private String fieldStoppedProducingYear;

  private String capitalProjectScopeDelivered;

  private DriveMechanism currentDriveMechanism;
  private String otherDriveMechanism;

  private String fdpTitle;
  private String fdpDateDay;
  private String fdpDateMonth;
  private String fdpDateYear;

  private List<UploadedFileForm> fieldReport = new ArrayList<>();

  public Boolean getProspectDiscoveryLocatedInUkcs() {
    return prospectDiscoveryLocatedInUkcs;
  }

  public void setProspectDiscoveryLocatedInUkcs(Boolean prospectDiscoveryLocatedInUkcs) {
    this.prospectDiscoveryLocatedInUkcs = prospectDiscoveryLocatedInUkcs;
  }

  public Boolean getProspectDiscoveryLocatedInEngland() {
    return prospectDiscoveryLocatedInEngland;
  }

  public void setProspectDiscoveryLocatedInEngland(Boolean prospectDiscoveryLocatedInEngland) {
    this.prospectDiscoveryLocatedInEngland = prospectDiscoveryLocatedInEngland;
  }

  public DecimalInput getPercentageLocatedInUkcs() {
    return percentageLocatedInUkcs;
  }

  public void setPercentageLocatedInUkcs(DecimalInput percentageLocatedInUkcs) {
    this.percentageLocatedInUkcs = percentageLocatedInUkcs;
  }

  public DecimalInput getPercentageLocatedInEngland() {
    return percentageLocatedInEngland;
  }

  public void setPercentageLocatedInEngland(
      DecimalInput percentageLocatedInEngland) {
    this.percentageLocatedInEngland = percentageLocatedInEngland;
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

  public String getFieldStoppedProducingDay() {
    return fieldStoppedProducingDay;
  }

  public void setFieldStoppedProducingDay(String fieldStoppedProducingDay) {
    this.fieldStoppedProducingDay = fieldStoppedProducingDay;
  }

  public String getFieldStoppedProducingMonth() {
    return fieldStoppedProducingMonth;
  }

  public void setFieldStoppedProducingMonth(String fieldStoppedProducingMonth) {
    this.fieldStoppedProducingMonth = fieldStoppedProducingMonth;
  }

  public String getFieldStoppedProducingYear() {
    return fieldStoppedProducingYear;
  }

  public void setFieldStoppedProducingYear(String fieldStoppedProducingYear) {
    this.fieldStoppedProducingYear = fieldStoppedProducingYear;
  }

  public String getCapitalProjectScopeDelivered() {
    return capitalProjectScopeDelivered;
  }

  public void setCapitalProjectScopeDelivered(String capitalProjectScopeDelivered) {
    this.capitalProjectScopeDelivered = capitalProjectScopeDelivered;
  }

  public DriveMechanism getCurrentDriveMechanism() {
    return currentDriveMechanism;
  }

  public void setCurrentDriveMechanism(DriveMechanism currentDriveMechanism) {
    this.currentDriveMechanism = currentDriveMechanism;
  }

  public String getOtherDriveMechanism() {
    return otherDriveMechanism;
  }

  public void setOtherDriveMechanism(String otherDriveMechanism) {
    this.otherDriveMechanism = otherDriveMechanism;
  }

  public String getFdpTitle() {
    return fdpTitle;
  }

  public void setFdpTitle(String fdpTitle) {
    this.fdpTitle = fdpTitle;
  }

  public String getFdpDateDay() {
    return fdpDateDay;
  }

  public void setFdpDateDay(String fdpDateDay) {
    this.fdpDateDay = fdpDateDay;
  }

  public String getFdpDateMonth() {
    return fdpDateMonth;
  }

  public void setFdpDateMonth(String fdpDateMonth) {
    this.fdpDateMonth = fdpDateMonth;
  }

  public String getFdpDateYear() {
    return fdpDateYear;
  }

  public void setFdpDateYear(String fdpDateYear) {
    this.fdpDateYear = fdpDateYear;
  }

  public List<UploadedFileForm> getFieldReport() {
    return fieldReport;
  }

  public void setFieldReport(List<UploadedFileForm> fieldReport) {
    this.fieldReport = fieldReport;
  }
}
