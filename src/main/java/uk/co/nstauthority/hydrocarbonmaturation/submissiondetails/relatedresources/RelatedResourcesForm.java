package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class RelatedResourcesForm implements SubmissionForm {
  private Boolean relatedToAnotherResource;

  private String submissionDetailIdOfRelatedResource;

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
}
