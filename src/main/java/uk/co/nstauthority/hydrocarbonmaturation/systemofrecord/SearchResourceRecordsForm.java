package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

class SearchResourceRecordsForm {
  private String generalDetailIdForResourceSubmission;
  private Integer responsibleOrganisationId;

  public String getGeneralDetailIdForResourceSubmission() {
    return generalDetailIdForResourceSubmission;
  }

  public void setGeneralDetailIdForResourceSubmission(String generalDetailIdForResourceSubmission) {
    this.generalDetailIdForResourceSubmission = generalDetailIdForResourceSubmission;
  }

  public Integer getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(Integer responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
  }
}
