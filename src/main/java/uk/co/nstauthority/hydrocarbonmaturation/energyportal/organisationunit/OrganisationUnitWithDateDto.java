package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.time.LocalDate;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

public record OrganisationUnitWithDateDto(Integer organisationUnitId,
                                          String organisationUnitName,
                                          String companyHouseNumber,
                                          List<OrganisationNameHistoryDto> organisationNameHistoryDtoList,
                                          LocalDate organisationNameDate) implements SearchSelectable {

  public String getDisplayText() {
    String displayName = DateUtil.findLatestItemForGivenStartDate(
        this.organisationNameHistoryDtoList(),
        organisationNameDate.atStartOfDay(),
        organisationNameHistoryDto -> organisationNameHistoryDto.startDate().atStartOfDay()
    ).map(OrganisationNameHistoryDto::name).orElse(null);

    if (companyHouseNumber != null) {
      return "%s (%s)".formatted(displayName, companyHouseNumber);
    }
    return displayName;
  }

  @Override
  public String getSelectionId() {
    return organisationUnitId.toString();
  }

  @Override
  public String getSelectionText() {
    return getDisplayText();
  }

  public static OrganisationUnitWithDateDto fromOrganisationUnitAndDate(
      OrganisationUnitDto organisationUnit,
      LocalDate organisationNameDate
  ) {
    return new OrganisationUnitWithDateDto(
        organisationUnit.organisationUnitId(),
        organisationUnit.organisationUnitName(),
        organisationUnit.companyHouseNumber(),
        organisationUnit.organisationNameHistoryDtoList(),
        organisationNameDate
    );
  }

}