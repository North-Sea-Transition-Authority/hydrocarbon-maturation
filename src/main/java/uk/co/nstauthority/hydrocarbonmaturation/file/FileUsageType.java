package uk.co.nstauthority.hydrocarbonmaturation.file;

public enum FileUsageType {
  SUBMISSION_DETAIL("SubmissionDetail");

  private final String usageType;

  FileUsageType(String usageType) {
    this.usageType = usageType;
  }

  public String getUsageType() {
    return usageType;
  }
}
