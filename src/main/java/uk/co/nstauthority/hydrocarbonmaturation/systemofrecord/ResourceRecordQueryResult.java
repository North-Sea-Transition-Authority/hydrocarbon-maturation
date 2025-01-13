package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;

public record ResourceRecordQueryResult(
    UUID resourceId,
    UUID submissionDetailId,
    Instant submissionAcceptedTimestamp,
    ResourceType resourceType,
    Integer fieldIdentifier,
    String resourceName,
    String centreOfInterestQuadBlock,
    Integer organisationResponsibleId,
    List<Integer> licencesTheResourceIsOn,
    ResponsibleTeamType responsibleTeam,
    String parentFieldName,
    FieldStatus fieldStatus,
    String licenceApplicationReference,
    boolean archived) {
  public ResourceRecordQueryResult {
    if (parentFieldName == null) {
      parentFieldName = "No parent field";
    }
  }
}
