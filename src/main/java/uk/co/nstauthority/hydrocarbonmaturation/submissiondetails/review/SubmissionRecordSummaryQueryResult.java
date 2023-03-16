package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionRecordSummaryQueryResult {

  private final UUID submissionDetailId;
  private final SubmissionType submissionType;
  private final ResourceType resourceType;
  private final String resourceName;
  private final String centreOfInterestQuadBlock;
  private final Integer organisationResponsibleId;
  private final ResponsibleTeamType responsibleTeam;
  private final List<Integer> licencesTheResourceIsOn;
  private final Timestamp createdOn;
  private final Timestamp submittedOn;


  SubmissionRecordSummaryQueryResult(UUID submissionDetailId,
                                     String submissionType,
                                     String resourceType,
                                     String resourceName,
                                     String centreOfInterestQuadBlock,
                                     Integer organisationResponsibleId,
                                     String responsibleTeam,
                                     List<Integer> licencesTheResourceIsOn,
                                     Timestamp createdOn,
                                     Timestamp submittedOn) {
    this.submissionDetailId = submissionDetailId;
    this.submissionType = SubmissionType.valueOf(submissionType);
    this.resourceType = ResourceType.valueOf(resourceType);
    this.resourceName = resourceName;
    this.centreOfInterestQuadBlock = centreOfInterestQuadBlock;
    this.organisationResponsibleId = organisationResponsibleId;
    this.responsibleTeam = ResponsibleTeamType.fromName(responsibleTeam);
    this.licencesTheResourceIsOn = licencesTheResourceIsOn;
    this.createdOn = createdOn;
    this.submittedOn = submittedOn;
  }

  UUID getSubmissionDetailId() {
    return submissionDetailId;
  }

  SubmissionType getSubmissionType() {
    return submissionType;
  }

  ResourceType getResourceType() {
    return resourceType;
  }

  String getResourceName() {
    return resourceName;
  }

  String getCentreOfInterestQuadBlock() {
    return centreOfInterestQuadBlock;
  }

  Integer getOrganisationResponsibleId() {
    return organisationResponsibleId;
  }

  ResponsibleTeamType getResponsibleTeam() {
    return responsibleTeam;
  }

  List<Integer> getLicencesTheResourceIsOn() {
    return licencesTheResourceIsOn;
  }

  Timestamp getCreatedOn() {
    return createdOn;
  }

  Timestamp getSubmittedOn() {
    return submittedOn;
  }
}
