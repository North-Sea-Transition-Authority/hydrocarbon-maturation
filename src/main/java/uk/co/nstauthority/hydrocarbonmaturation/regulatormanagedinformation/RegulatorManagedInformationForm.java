package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.ArrayList;
import java.util.List;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

public class RegulatorManagedInformationForm {

  private GeographicalArea geographicalArea;
  private List<UploadedFileForm> relinquishmentReport = new ArrayList<>();
  private ResponsibleTeamType responsibleTeamType;
  private String resourceName;
  private Boolean linkedToShelvedCopField;
  private String fieldSelectedOption;
  private String well;
  private HydrocarbonsDiscovered hydrocarbonCase;
  private OpportunityType opportunityType;
  private String otherTypeOfOpportunity;

  public GeographicalArea getGeographicalArea() {
    return geographicalArea;
  }

  public void setGeographicalArea(
      GeographicalArea geographicalArea) {
    this.geographicalArea = geographicalArea;
  }

  public List<UploadedFileForm> getRelinquishmentReport() {
    return relinquishmentReport;
  }

  public void setRelinquishmentReport(
      List<UploadedFileForm> relinquishmentReport) {
    this.relinquishmentReport = relinquishmentReport;
  }

  public ResponsibleTeamType getResponsibleTeamType() {
    return responsibleTeamType;
  }

  public void setResponsibleTeamType(
      ResponsibleTeamType responsibleTeamType) {
    this.responsibleTeamType = responsibleTeamType;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public Boolean getLinkedToShelvedCopField() {
    return linkedToShelvedCopField;
  }

  public void setLinkedToShelvedCopField(Boolean linkedToShelvedCopField) {
    this.linkedToShelvedCopField = linkedToShelvedCopField;
  }

  public String getFieldSelectedOption() {
    return fieldSelectedOption;
  }

  public void setFieldSelectedOption(String fieldSelectedOption) {
    this.fieldSelectedOption = fieldSelectedOption;
  }

  public String getWell() {
    return well;
  }

  public void setWell(String well) {
    this.well = well;
  }

  public HydrocarbonsDiscovered getHydrocarbonCase() {
    return hydrocarbonCase;
  }

  public void setHydrocarbonCase(
      HydrocarbonsDiscovered hydrocarbonCase) {
    this.hydrocarbonCase = hydrocarbonCase;
  }

  public OpportunityType getOpportunityType() {
    return opportunityType;
  }

  public void setOpportunityType(
      OpportunityType opportunityType) {
    this.opportunityType = opportunityType;
  }

  public String getOtherTypeOfOpportunity() {
    return otherTypeOfOpportunity;
  }

  public void setOtherTypeOfOpportunity(String otherTypeOfOpportunity) {
    this.otherTypeOfOpportunity = otherTypeOfOpportunity;
  }
}
