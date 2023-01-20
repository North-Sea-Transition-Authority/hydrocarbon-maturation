package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedprospects;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class RelatedProspectsForm implements SubmissionForm {
  private Boolean relatedToAnotherProspect;

  private String generalDetailIdOfRelatedProspect;

  private Boolean preferredView;

  public Boolean getRelatedToAnotherProspect() {
    return relatedToAnotherProspect;
  }

  public void setRelatedToAnotherProspect(Boolean relatedToAnotherProspect) {
    this.relatedToAnotherProspect = relatedToAnotherProspect;
  }

  public String getGeneralDetailIdOfRelatedProspect() {
    return generalDetailIdOfRelatedProspect;
  }

  public void setGeneralDetailIdOfRelatedProspect(String generalDetailIdOfRelatedProspect) {
    this.generalDetailIdOfRelatedProspect = generalDetailIdOfRelatedProspect;
  }

  public Boolean getPreferredView() {
    return preferredView;
  }

  public void setPreferredView(Boolean preferredView) {
    this.preferredView = preferredView;
  }
}
