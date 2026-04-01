package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public record UndevelopedDiscoveryResourceDto(
    UUID resourceId,
    String resourceName,
    SubmissionType submissionType,
    GeographicalArea geographicalArea,
    List<Integer> licenceIds
) {
}
