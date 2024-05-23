package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;

public record ResourceRecordSummaryView(
    UUID submissionDetailId,
    String resourceName,
    String centreOfInterest,
    String licenceApplicationReference,
    Integer responsibleOrganisationId,
    String organisationResponsible,
    List<Integer> licenceIds,
    List<String> licences,
    ResponsibleTeamType responsibleTeam,
    String parentFieldName,
    String fieldStatus
) {
}
