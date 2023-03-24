package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class RelatedResourcesForm implements SubmissionForm {
  private Boolean relatedToAnotherResource;

  private String submissionDetailIdOfRelatedResource;

  private Boolean preferredView;

  public Boolean getRelatedToAnotherResource() {
    return relatedToAnotherResource;
  }

  public void setRelatedToAnotherResource(Boolean relatedToAnotherResource) {
    this.relatedToAnotherResource = relatedToAnotherResource;
  }

  public String getSubmissionDetailIdOfRelatedResource() {
    return submissionDetailIdOfRelatedResource;
  }

  public void setSubmissionDetailIdOfRelatedResource(String submissionDetailIdOfRelatedResource) {
    this.submissionDetailIdOfRelatedResource = submissionDetailIdOfRelatedResource;
  }

  public Boolean getPreferredView() {
    return preferredView;
  }

  public void setPreferredView(Boolean preferredView) {
    this.preferredView = preferredView;
  }
}
