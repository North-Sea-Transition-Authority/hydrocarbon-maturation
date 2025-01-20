package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.UUID;

public record SubmissionQueryResult(
    UUID resourceDetailId,
    String resourceName,
    String centreOfInterestQuadrantAndBlock
) {
}
