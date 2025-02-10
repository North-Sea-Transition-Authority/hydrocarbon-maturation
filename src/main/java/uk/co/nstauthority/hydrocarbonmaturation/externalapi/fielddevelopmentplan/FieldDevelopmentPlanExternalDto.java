package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FieldDevelopmentPlanExternalDto(
    Integer fieldId,
    UUID submissionId,
    LocalDate date,
    String title,
    List<FieldDevelopmentPlanAddendumExternalDto> fieldDevelopmentPlanAddendums
) {
  static FieldDevelopmentPlanExternalDto empty() {
    return new FieldDevelopmentPlanExternalDto(null, null, null, null, null);
  }
}
