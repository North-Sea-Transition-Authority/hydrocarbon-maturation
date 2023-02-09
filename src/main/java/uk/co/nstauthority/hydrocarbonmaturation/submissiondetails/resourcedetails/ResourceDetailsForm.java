package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

public class ResourceDetailsForm implements SubmissionForm {

  private String resourceName;

  private String responsibleOrganisationId;

  private OnshoreOffshore onshoreOffshore;

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(String responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(
      OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }
}
