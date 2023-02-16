package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.ArrayList;
import java.util.List;

class SearchResourceRecordsForm {

  private String resourceDetailIdForResourceSubmission;
  private Integer responsibleOrganisationId;
  private List<String> resourceTypes = new ArrayList<>();
  private List<String> opportunityTypes = new ArrayList<>();
  private String licenceRound;
  private String centreOfInterestBlock;
  private String licenceId;

  public String getResourceDetailIdForResourceSubmission() {
    return resourceDetailIdForResourceSubmission;
  }

  public void setResourceDetailIdForResourceSubmission(String resourceDetailIdForResourceSubmission) {
    this.resourceDetailIdForResourceSubmission = resourceDetailIdForResourceSubmission;
  }

  public Integer getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(Integer responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
  }

  public String getCentreOfInterestBlock() {
    return centreOfInterestBlock;
  }

  public void setCentreOfInterestBlock(String centreOfInterestBlock) {
    this.centreOfInterestBlock = centreOfInterestBlock;
  }

  public String getLicenceRound() {
    return licenceRound;
  }

  public void setLicenceRound(String licenceRound) {
    this.licenceRound = licenceRound;
  }

  public String getLicenceId() {
    return licenceId;
  }

  public void setLicenceId(String licenceId) {
    this.licenceId = licenceId;
  }

  public List<String> getResourceTypes() {
    return resourceTypes;
  }

  public void setResourceTypes(List<String> resourceTypes) {
    this.resourceTypes = resourceTypes;
  }

  public List<String> getOpportunityTypes() {
    return opportunityTypes;
  }

  public void setOpportunityTypes(
      List<String> opportunityTypes) {
    this.opportunityTypes = opportunityTypes;
  }
}
