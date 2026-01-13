package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

public class RegulatorReportingInformationForm {

  private String resourceName;
  private String well;
  private HydrocarbonsDiscovered hydrocarbonCase;
  private OpportunityType opportunityType;
  private String otherTypeOfOpportunity;

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
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
