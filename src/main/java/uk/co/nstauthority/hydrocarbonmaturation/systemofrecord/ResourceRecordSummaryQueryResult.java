package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;

record ResourceRecordSummaryQueryResult(
    UUID submissionDetailId,
    ResourceType resourceType,
    String resourceName,
    String centreOfInterestQuadBlock,
    String licenceApplicationReference,
    Integer prospectOrDiscoveryResponsibleOrganisationId,
    ResponsibleTeamType responsibleTeam,
    List<Integer> licencesTheResourceIsOn,
    Integer fieldId,
    Integer parentFieldId,
    FieldStatus fieldStatus
) {
}
