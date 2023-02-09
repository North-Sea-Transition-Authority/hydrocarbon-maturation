package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

class SearchResourceRecordsForm {
  private String resourceDetailIdForResourceSubmission;
  private Integer responsibleOrganisationId;

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
}
