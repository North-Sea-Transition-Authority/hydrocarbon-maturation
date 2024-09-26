package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.time.LocalDate;
import uk.co.fivium.energyportalapi.generated.types.OrganisationNameHistory;

public record OrganisationNameHistoryDto(String name, LocalDate startDate, LocalDate endDate) {

  public static OrganisationNameHistoryDto fromOrganisationNameHistory(OrganisationNameHistory organisationNameHistory) {
    return new OrganisationNameHistoryDto(
        organisationNameHistory.getName(),
        organisationNameHistory.getStartDate(),
        organisationNameHistory.getEndDate()
    );
  }
}
