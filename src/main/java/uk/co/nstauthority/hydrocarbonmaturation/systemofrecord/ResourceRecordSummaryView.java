package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.UUID;

public record ResourceRecordSummaryView(
    UUID submissionDetailId,
    String resourceNameAndCentreOfInterest,
    Integer responsibleOrganisationId,
    String organisationResponsible,
    List<Integer> licenceIds,
    List<String> licences,
    String responsibleTeam
) {
}
