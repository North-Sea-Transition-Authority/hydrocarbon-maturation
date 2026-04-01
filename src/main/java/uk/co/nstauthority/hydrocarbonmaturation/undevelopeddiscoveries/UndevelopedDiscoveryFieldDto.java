package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.time.LocalDateTime;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

public record UndevelopedDiscoveryFieldDto(
    UUID discoveryResourceId,
    FieldStatus fieldStatus,
    LocalDateTime fieldStatusDate
) {
}
