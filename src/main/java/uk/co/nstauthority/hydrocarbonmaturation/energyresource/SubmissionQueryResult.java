package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.UUID;

public record SubmissionQueryResult(
    UUID resourceId,
    String resourceName,
    String centreOfInterestQuadrantAndBlock,
    String licenceApplicationReference
) {
}
