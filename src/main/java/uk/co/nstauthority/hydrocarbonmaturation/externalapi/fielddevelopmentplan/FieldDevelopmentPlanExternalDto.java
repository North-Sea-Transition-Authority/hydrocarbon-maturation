package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import java.time.LocalDate;
import java.util.List;

public record FieldDevelopmentPlanExternalDto(
    LocalDate date,
    String title,
    List<FieldDevelopmentPlanAddendumExternalDto> fieldDevelopmentPlanAddendums
) {
  static FieldDevelopmentPlanExternalDto empty() {
    return new FieldDevelopmentPlanExternalDto(null, null, null);
  }
}
