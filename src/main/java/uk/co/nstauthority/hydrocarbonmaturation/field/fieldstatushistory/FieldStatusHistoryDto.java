package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

public record FieldStatusHistoryDto(
    String removeUrl,
    String fieldStatus,
    String fieldStatusDate
) {
}
