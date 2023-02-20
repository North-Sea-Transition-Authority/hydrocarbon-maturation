package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;

class ResourceRecordSummaryQueryResult {

  private final UUID submissionDetailId;
  private final ResourceType resourceType;
  private final String resourceName;
  private final String centreOfInterestQuadBlock;
  private final Integer organisationResponsibleId;
  private final ResponsibleTeamType responsibleTeam;
  private final List<Integer> licencesTheResourceIsOn;
  private final Timestamp createdOn;
  private final Timestamp approvedOn;

  ResourceRecordSummaryQueryResult(UUID submissionDetailId,
                            String resourceType,
                            String resourceName,
                            String centreOfInterestQuadBlock,
                            Integer organisationResponsibleId,
                            String responsibleTeam,
                            List<Integer> licencesTheResourceIsOn,
                            Timestamp createdOn,
                            Timestamp approvedOn) {
    this.submissionDetailId = submissionDetailId;
    this.resourceType = ResourceType.valueOf(resourceType);
    this.resourceName = resourceName;
    this.centreOfInterestQuadBlock = centreOfInterestQuadBlock;
    this.organisationResponsibleId = organisationResponsibleId;
    this.responsibleTeam = ResponsibleTeamType.fromName(responsibleTeam);
    this.licencesTheResourceIsOn = licencesTheResourceIsOn;
    this.createdOn = createdOn;
    this.approvedOn = approvedOn;
  }

  UUID getSubmissionDetailId() {
    return submissionDetailId;
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

  Timestamp getApprovedOn() {
    return approvedOn;
  }
}
