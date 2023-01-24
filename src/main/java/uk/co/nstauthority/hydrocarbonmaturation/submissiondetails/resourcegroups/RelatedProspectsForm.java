package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class RelatedProspectsForm implements SubmissionForm {
  private Boolean relatedToAnotherProspect;

  private String submissionDetailIdOfRelatedProspect;

  private Boolean preferredView;

  public Boolean getRelatedToAnotherProspect() {
    return relatedToAnotherProspect;
  }

  public void setRelatedToAnotherProspect(Boolean relatedToAnotherProspect) {
    this.relatedToAnotherProspect = relatedToAnotherProspect;
  }

  public String getSubmissionDetailIdOfRelatedProspect() {
    return submissionDetailIdOfRelatedProspect;
  }

  public void setSubmissionDetailIdOfRelatedProspect(String submissionDetailIdOfRelatedProspect) {
    this.submissionDetailIdOfRelatedProspect = submissionDetailIdOfRelatedProspect;
  }

  public Boolean getPreferredView() {
    return preferredView;
  }

  public void setPreferredView(Boolean preferredView) {
    this.preferredView = preferredView;
  }
}
