package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum SubmissionDetailStatus implements Displayable {

  DRAFT("Draft", 10),
  APPROVED("Approved", 20),
  SUBMITTED("Submitted", 30),
  DELETED("Deleted", 40);

  private final String displayName;
  private final int displayOrder;

  SubmissionDetailStatus(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }
}
