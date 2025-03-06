package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;

public record ResourceRecordSummaryView(
    ResourceType resourceType,
    UUID submissionDetailId,
    String resourceName,
    String centreOfInterest,
    String licenceApplicationReference,
    Integer responsibleOrganisationId,
    String organisationResponsible,
    List<Integer> licenceIds,
    List<String> licences,
    ResponsibleTeamType responsibleTeam,
    Integer fieldId,
    String parentFieldName,
    String fieldStatus
) {
}
