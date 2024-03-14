package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

public class ProjectForm {
  private String projectName;
  private ProjectType projectType;
  private ProjectStatus projectStatus;
  private Boolean isPartOfPipeline;
  private String selectedSubmissionId;
  private String fdpaStartUpDateDay;
  private String fdpaStartUpDateMonth;
  private String fdpaStartUpDateYear;

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public ProjectType getProjectType() {
    return projectType;
  }

  public void setProjectType(ProjectType projectType) {
    this.projectType = projectType;
  }

  public ProjectStatus getProjectStatus() {
    return projectStatus;
  }

  public void setProjectStatus(ProjectStatus projectStatus) {
    this.projectStatus = projectStatus;
  }

  public Boolean getIsPartOfPipeline() {
    return isPartOfPipeline;
  }

  public void setIsPartOfPipeline(Boolean partOfPipeline) {
    isPartOfPipeline = partOfPipeline;
  }

  public String getSelectedSubmissionId() {
    return selectedSubmissionId;
  }

  public void setSelectedSubmissionId(String selectedSubmissionId) {
    this.selectedSubmissionId = selectedSubmissionId;
  }

  public String getFdpaStartUpDateDay() {
    return fdpaStartUpDateDay;
  }

  public void setFdpaStartUpDateDay(String fdpaStartUpDateDay) {
    this.fdpaStartUpDateDay = fdpaStartUpDateDay;
  }

  public String getFdpaStartUpDateMonth() {
    return fdpaStartUpDateMonth;
  }

  public void setFdpaStartUpDateMonth(String fdpaStartUpDateMonth) {
    this.fdpaStartUpDateMonth = fdpaStartUpDateMonth;
  }

  public String getFdpaStartUpDateYear() {
    return fdpaStartUpDateYear;
  }

  public void setFdpaStartUpDateYear(String fdpaStartUpDateYear) {
    this.fdpaStartUpDateYear = fdpaStartUpDateYear;
  }
}
