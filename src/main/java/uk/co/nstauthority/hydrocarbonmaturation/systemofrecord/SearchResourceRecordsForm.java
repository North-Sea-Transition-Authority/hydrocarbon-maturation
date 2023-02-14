package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

class SearchResourceRecordsForm {

  private String resourceDetailIdForResourceSubmission;
  private Integer responsibleOrganisationId;
  private String centreOfInterestBlock;
  private boolean prospectResourceType;
  private boolean discoveryResourceType;

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

  public boolean isProspectResourceType() {
    return prospectResourceType;
  }

  public void setProspectResourceType(boolean prospectResourceType) {
    this.prospectResourceType = prospectResourceType;
  }

  public boolean isDiscoveryResourceType() {
    return discoveryResourceType;
  }

  public void setDiscoveryResourceType(boolean discoveryResourceType) {
    this.discoveryResourceType = discoveryResourceType;
  }

  public String getCentreOfInterestBlock() {
    return centreOfInterestBlock;
  }

  public void setCentreOfInterestBlock(String centreOfInterestBlock) {
    this.centreOfInterestBlock = centreOfInterestBlock;
  }
}
