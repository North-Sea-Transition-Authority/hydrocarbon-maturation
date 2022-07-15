package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

enum SubmissionType {
  CREATE_PROSPECT("Create a new prospect"),
  PROSPECT_UPDATE("Update an existing prospect");

  private final String selectionDisplayName;

  SubmissionType(String selectionDisplayName) {
    this.selectionDisplayName = selectionDisplayName;
  }

  public String getSelectionDisplayName() {
    return selectionDisplayName;
  }
}
