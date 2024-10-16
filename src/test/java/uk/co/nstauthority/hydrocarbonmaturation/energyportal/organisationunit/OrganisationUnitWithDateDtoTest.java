package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganisationUnitWithDateDtoTest {

  private static final String REGISTERED_NUMBER = "0000999";
  private static final String NEW_NAME = "new name";
  private static final String OLD_NAME = "old name";

  @Test
  void fromOrganisationUnitAndDate() {
    var localDate = LocalDate.now();
    var organisationUnitDto = createOrganisationUnitDto(REGISTERED_NUMBER);

    var organisationUnitWithSubmissionDetailDto = OrganisationUnitWithDateDto.fromOrganisationUnitAndDate(
        organisationUnitDto,
        localDate
    );

    assertThat(organisationUnitWithSubmissionDetailDto).extracting(
        OrganisationUnitWithDateDto::organisationUnitId,
        OrganisationUnitWithDateDto::organisationUnitName,
        OrganisationUnitWithDateDto::companyHouseNumber,
        OrganisationUnitWithDateDto::organisationNameHistoryDtoList,
        OrganisationUnitWithDateDto::organisationNameDate
    ).containsExactly(
        organisationUnitDto.organisationUnitId(),
        organisationUnitDto.organisationUnitName(),
        organisationUnitDto.companyHouseNumber(),
        organisationUnitDto.organisationNameHistoryDtoList(),
        localDate
    );
  }

  @Test
  void getDisplayText_whenRegisteredNumberExists() {
    var acceptedLocalDate = LocalDate.of(2023, 6, 1);
    var organisationUnitDto = createOrganisationUnitDto(REGISTERED_NUMBER);

    assertThat(OrganisationUnitWithDateDto.fromOrganisationUnitAndDate(
        organisationUnitDto,
        acceptedLocalDate
    ).getDisplayText()).isEqualTo(String.format("%s (%s)", OLD_NAME, REGISTERED_NUMBER));
  }

  @Test
  void getDisplayText_whenRegisteredNumberDoesntExist() {
    var acceptedLocalDate = LocalDate.of(2023, 6, 1);
    var organisationUnitDto = createOrganisationUnitDto(null);

    assertThat(OrganisationUnitWithDateDto.fromOrganisationUnitAndDate(
        organisationUnitDto,
        acceptedLocalDate
    ).getDisplayText()).isEqualTo(OLD_NAME);
  }

  private OrganisationUnitDto createOrganisationUnitDto(String registrationNumber) {
    var organisationUnitId = 1;
    List<OrganisationNameHistoryDto> organisationNameHistoryList = new ArrayList<>();
    var organisationNameHistory1 = new OrganisationNameHistoryDto(
        OLD_NAME,
        LocalDate.of(2023, 1, 1),
        LocalDate.of(2024, 1, 1)
    );
    var organisationNameHistory2 = new OrganisationNameHistoryDto(
        NEW_NAME,
        LocalDate.of(2024, 1, 2),
        null
    );
    organisationNameHistoryList.add(organisationNameHistory1);
    organisationNameHistoryList.add(organisationNameHistory2);
    return new OrganisationUnitDto(
        organisationUnitId,
        NEW_NAME,
        registrationNumber,
        organisationNameHistoryList
    );
  }
}
