package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import java.time.LocalDate;

public record FieldDevelopmentPlanAddendumExternalDto(
    LocalDate date,
    String title
) {
}
