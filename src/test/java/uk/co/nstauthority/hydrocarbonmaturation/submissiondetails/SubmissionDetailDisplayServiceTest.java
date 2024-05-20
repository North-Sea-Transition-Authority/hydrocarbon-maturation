package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailDisplayServiceTest {
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @Mock
  private LicenceQueryService licenceQueryService;
  @InjectMocks
  private SubmissionDetailDisplayService underTest;

  @Nested
  class WhenGettingOrganisationResponsibleDisplay {
    @Test
    void organisationResponsibleIdIsNull_DisplaysNotProvided() {
      var result = underTest.getOrganisationResponsibleDisplayById(null);

      assertThat(result).isEqualTo(SubmissionDetailDisplayService.NOT_PROVIDED);
    }

    @Test
    void findOrganisationUnitReturnsEmpty_DisplaysNotProvided() {
      var organisationResponsibleId = 1;
      when(organisationUnitQueryService.findOrganisationUnit(organisationResponsibleId)).thenReturn(Optional.empty());

      var result = underTest.getOrganisationResponsibleDisplayById(organisationResponsibleId);

      assertThat(result).isEqualTo(SubmissionDetailDisplayService.NOT_PROVIDED);
    }

    @Test
    void organsiationUnitNameAndRegisteredNumberAreNull_DisplaysNotProvided() {
      var organisationResponsibleId = 1;
      var organisationUnitDto = new OrganisationUnitDto(null, null, null);
      when(organisationUnitQueryService.findOrganisationUnit(organisationResponsibleId)).thenReturn(Optional.of(organisationUnitDto));

      var result = underTest.getOrganisationResponsibleDisplayById(organisationResponsibleId);

      assertThat(result).isEqualTo(SubmissionDetailDisplayService.NOT_PROVIDED);
    }

    @Test
    void organsiationUnitNameAndRegisteredNumberArePresent_DisplaysNameAndRegNumber() {
      var organisationResponsibleId = 1;
      var organisationUnitDto = new OrganisationUnitDto(
          1,
          "name",
          "123"
      );
      when(organisationUnitQueryService.findOrganisationUnit(organisationResponsibleId)).thenReturn(Optional.of(organisationUnitDto));

      var result = underTest.getOrganisationResponsibleDisplayById(organisationResponsibleId);

      assertThat(result).isEqualTo("name");
    }

    @Test
    void organsiationUnitNameIsPresent_DisplaysName() {
      var organisationResponsibleId = 1;
      var organisationUnitDto = new OrganisationUnitDto(
          organisationResponsibleId,
          "name",
          null
      );
      when(organisationUnitQueryService.findOrganisationUnit(organisationResponsibleId)).thenReturn(Optional.of(organisationUnitDto));

      var result = underTest.getOrganisationResponsibleDisplayById(organisationResponsibleId);

      assertThat(result).isEqualTo("name");
    }
  }

  @Nested
  class WhenGettingOrganisationResponsibleDisplayFromMap {

    private final Map<Integer, OrganisationUnitDto> organisationUnitMap = Map.of(
      1, new OrganisationUnitDto(1, "name1", "123"),
      2, new OrganisationUnitDto(2, "name2", "456")
    );

    @Test
    void organisationResponsibleIdIsNull_DisplaysNotProvided() {
      var result = underTest.getOrganisationResponsibleDisplayFromMap(organisationUnitMap, null);

      assertThat(result).isEqualTo(SubmissionDetailDisplayService.NOT_PROVIDED);
    }

    @Test
    void mapDoesNotContainOrganisationResponsibleId_DisplaysNotProvided() {
      var organisationResponsibleId = 3;

      var result = underTest.getOrganisationResponsibleDisplayFromMap(organisationUnitMap, organisationResponsibleId);

      assertThat(result).isEqualTo(SubmissionDetailDisplayService.NOT_PROVIDED);
    }

    @Test
    void mapContainsOrganisationResponsibleId_DisplaysName() {
      var organisationResponsibleId = 2;

      var result = underTest.getOrganisationResponsibleDisplayFromMap(organisationUnitMap, organisationResponsibleId);

      assertThat(result).isEqualTo("name2");
    }
  }

  @Nested
  class WhenGettingLicencesForDisplay {

    @Test
    void licenceIdsIsNull_DisplaysNotProvided() {
      var result = underTest.getLicencesForDisplay(null);

      assertThat(result).isEqualTo(List.of(SubmissionDetailDisplayService.NOT_PROVIDED));
    }

    @Test
    void licenceIdsIsEmpty_DisplaysNotProvided() {
      List<Integer> licenceIds = List.of();

      var result = underTest.getLicencesForDisplay(licenceIds);

      assertThat(result).isEqualTo(List.of(SubmissionDetailDisplayService.NOT_PROVIDED));
    }

    @Test
    void multipleLicenseIds_DisplaysListOfLicenceRefs() {
      var licenceIds = List.of(1, 2);

      var licenceRef1 = "ref1";
      var licenceView1 = new LicenceView(licenceRef1, 1, LicenceStatus.EXTANT);
      var licenceRef2 = "ref2";
      var licenceView2 = new LicenceView(licenceRef2, 2, LicenceStatus.EXTANT);
      when(licenceQueryService.getLicencesByIds(List.of("1", "2")))
          .thenReturn(List.of(licenceView1, licenceView2));

      var result = underTest.getLicencesForDisplay(licenceIds);

      assertThat(result).isEqualTo(List.of(licenceRef1, licenceRef2));
    }
  }

  @Nested
  class WhenGettingLicencesForDisplayFromMap {

    private final String licenceRef1 = "ref1";
    private final String licenceRef2 = "ref2";
    private final LicenceView licenceView1 = new LicenceView(licenceRef1, 1, LicenceStatus.EXTANT);
    private final LicenceView licenceView2 = new LicenceView(licenceRef2, 2, LicenceStatus.EXTANT);


    private Map<Integer, LicenceView> licenceMap = Map.of(
        1, licenceView1,
        2, licenceView2
    );

    @BeforeEach
    void setup() {
    }

    @Test
    void licenceIdsIsNull_DisplaysNotProvided() {
      licenceMap = null;
      var result = underTest.getLicencesForDisplayFromMap(licenceMap, null);

      assertThat(result).isEqualTo(List.of(SubmissionDetailDisplayService.NOT_PROVIDED));
    }

    @Test
    void licenceIdsIsEmpty_DisplaysNotProvided() {
      List<Integer> licenceIds = List.of();

      var result = underTest.getLicencesForDisplayFromMap(licenceMap, licenceIds);

      assertThat(result).isEqualTo(List.of(SubmissionDetailDisplayService.NOT_PROVIDED));
    }

    @Test
    void multipleLicenseIds_DisplaysListOfLicenceRefs() {
      var licenceIds = List.of(1, 2, 3);

      var result = underTest.getLicencesForDisplayFromMap(licenceMap, licenceIds);

      assertThat(result).isEqualTo(List.of(licenceRef1, licenceRef2));
    }
  }
}