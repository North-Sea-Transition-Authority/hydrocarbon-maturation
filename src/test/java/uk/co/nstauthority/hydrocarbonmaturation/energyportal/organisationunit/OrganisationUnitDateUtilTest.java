package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganisationUnitDateUtilTest {

  @Test
  void getOrganisationNameHistoryForDate() {
    var oldOrganisationName = "old name";
    var currentOrganisationName = "new name";
    var organisationNameHistory = getOrganisationNameHistoryDtos(oldOrganisationName, currentOrganisationName);
    var organisationUnitDto = new OrganisationUnitDto(
        1,
        currentOrganisationName,
        "0000999",
        organisationNameHistory
    );
    var acceptedDate = LocalDate.of(2012, 6, 1);
    var nameHistoryForDate = OrganisationUnitDateUtil.getOrganisationNameHistoryForDate(organisationUnitDto, acceptedDate);
    assertThat(nameHistoryForDate).isPresent();
    assertThat(nameHistoryForDate.get().name()).isEqualTo(oldOrganisationName);
    assertThat(nameHistoryForDate.get().startDate()).isEqualTo(organisationNameHistory.get(0).startDate());
    assertThat(nameHistoryForDate.get().endDate()).isEqualTo(organisationNameHistory.get(0).endDate());
  }

  private List<OrganisationNameHistoryDto> getOrganisationNameHistoryDtos(String oldOrganisationName,
                                                                          String currentOrganisationName) {
    List<OrganisationNameHistoryDto> nameHistory = new ArrayList<>();
    var organisationNameHistory1 = new OrganisationNameHistoryDto(
        oldOrganisationName,
        LocalDate.of(2012, 1, 1),
        LocalDate.of(2013, 1, 1)
    );
    var organisationNameHistory2 = new OrganisationNameHistoryDto(
        currentOrganisationName,
        LocalDate.of(2013, 1, 2),
        null
    );
    nameHistory.add(organisationNameHistory1);
    nameHistory.add(organisationNameHistory2);
    return nameHistory;
  }
}
