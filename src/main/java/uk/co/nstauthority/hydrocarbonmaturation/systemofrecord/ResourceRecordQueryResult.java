package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;

class ResourceRecordQueryResult {

  private final UUID resourceId;
  private final UUID submissionDetailId;
  private final ResourceType resourceType;
  private final String resourceName;
  private final String centreOfInterestQuadBlock;
  private final Integer organisationResponsibleId;
  private final List<Integer> licencesTheResourceIsOn;
  private final ResponsibleTeamType responsibleTeam;

  ResourceRecordQueryResult(UUID resourceId,
                            UUID submissionDetailId,
                            String resourceType,
                            String resourceName,
                            String centreOfInterestQuadBlock,
                            Integer organisationResponsibleId,
                            List<Integer> licencesTheResourceIsOn,
                            ResponsibleTeamType responsibleTeam) {
    this.resourceId = resourceId;
    this.submissionDetailId = submissionDetailId;
    this.resourceType = ResourceType.valueOf(resourceType);
    this.resourceName = resourceName;
    this.centreOfInterestQuadBlock = centreOfInterestQuadBlock;
    this.organisationResponsibleId = organisationResponsibleId;
    this.licencesTheResourceIsOn = licencesTheResourceIsOn;
    this.responsibleTeam = responsibleTeam;
  }

  public UUID getResourceId() {
    return resourceId;
  }

  UUID getSubmissionDetailId() {
    return submissionDetailId;
  }

  public ResourceType getResourceType() {
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

  List<Integer> getLicencesTheResourceIsOn() {
    return licencesTheResourceIsOn;
  }

  public ResponsibleTeamType getResponsibleTeam() {
    return responsibleTeam;
  }
}
