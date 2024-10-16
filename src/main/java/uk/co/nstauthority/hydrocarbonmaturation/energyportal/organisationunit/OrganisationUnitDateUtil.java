package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.time.LocalDate;
import java.util.Optional;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

public class OrganisationUnitDateUtil {

  private OrganisationUnitDateUtil() {
    throw new IllegalStateException("Cannot instantiate utility class");
  }

  public static Optional<OrganisationNameHistoryDto> getOrganisationNameHistoryForDate(
      OrganisationUnitDto organisationUnit, LocalDate acceptedDate
  ) {
    return DateUtil.findLatestItemForGivenStartDate(
        organisationUnit.organisationNameHistoryDtoList(),
        acceptedDate.atStartOfDay(),
        organisationNameHistoryDto -> organisationNameHistoryDto.startDate().atStartOfDay()
    );
  }
}
