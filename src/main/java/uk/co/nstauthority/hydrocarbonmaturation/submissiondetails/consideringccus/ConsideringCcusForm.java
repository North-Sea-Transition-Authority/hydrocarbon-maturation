package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class ConsideringCcusForm implements SubmissionForm {
  private Boolean isConnectedToProject;
  private String connectedProjects;
  private Boolean isAnyShallowerHorizons;
  private String ageAndFormationOfHorizons;

  public Boolean getIsConnectedToProject() {
    return isConnectedToProject;
  }

  public void setIsConnectedToProject(Boolean connectedToProject) {
    isConnectedToProject = connectedToProject;
  }

  public String getConnectedProjects() {
    return connectedProjects;
  }

  public void setConnectedProjects(String connectedProjects) {
    this.connectedProjects = connectedProjects;
  }

  public Boolean getIsAnyShallowerHorizons() {
    return isAnyShallowerHorizons;
  }

  public void setIsAnyShallowerHorizons(Boolean anyShallowerHorizons) {
    isAnyShallowerHorizons = anyShallowerHorizons;
  }

  public String getAgeAndFormationOfHorizons() {
    return ageAndFormationOfHorizons;
  }

  public void setAgeAndFormationOfHorizons(String ageAndFormationOfHorizons) {
    this.ageAndFormationOfHorizons = ageAndFormationOfHorizons;
  }
}
