package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

enum SubmissionEstimateType {
  CSR("Estimated CSR first draft submission date"),
  FDP("Estimated FDP first draft submission date"),
  FDPA("Estimated FDPA first draft submission date"),
  COP("Estimated CoP date"),
  FIRST_PRODUCTION("Estimated first production date"),
  FDPA_START_UP("Estimated FDPA start-up date"),
  DECISION_DATE("Estimated decision date")
  ;

  private final String displayName;

  SubmissionEstimateType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
