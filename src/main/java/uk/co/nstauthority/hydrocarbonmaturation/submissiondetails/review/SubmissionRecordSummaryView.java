package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public record SubmissionRecordSummaryView(
    UUID submissionDetailId,
    SubmissionType submissionType,
    String resourceNameAndCentreOfInterest,
    Integer responsibleOrganisationId,
    String organisationResponsible,
    List<Integer> licenceIds,
    List<String> licences,
    String responsibleTeam,
    ResponsibleTeamType responsibleTeamType
) {
}
