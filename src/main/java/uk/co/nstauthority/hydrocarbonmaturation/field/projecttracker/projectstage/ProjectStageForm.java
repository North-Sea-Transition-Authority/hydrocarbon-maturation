package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

public class ProjectStageForm {
  private ProjectStage stage;

  private String stageDateDay;
  private String stageDateMonth;
  private String stageDateYear;

  public ProjectStage getStage() {
    return stage;
  }

  public void setStage(ProjectStage stage) {
    this.stage = stage;
  }

  public String getStageDateDay() {
    return stageDateDay;
  }

  public void setStageDateDay(String stageDateDay) {
    this.stageDateDay = stageDateDay;
  }

  public String getStageDateMonth() {
    return stageDateMonth;
  }

  public void setStageDateMonth(String stageDateMonth) {
    this.stageDateMonth = stageDateMonth;
  }

  public String getStageDateYear() {
    return stageDateYear;
  }

  public void setStageDateYear(String stageDateYear) {
    this.stageDateYear = stageDateYear;
  }
}
