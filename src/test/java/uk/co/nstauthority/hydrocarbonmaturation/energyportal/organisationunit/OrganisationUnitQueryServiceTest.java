package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class OrganisationUnitQueryServiceTest {

  private static final ServiceConfigurationProperties serviceConfigurationProperties = new ServiceConfigurationProperties(
      "name",
      "mnemonic"
  );
  private static final User USER = UserTestUtil.Builder().build();
  private static final Integer ORGANISATION_UNIT_ID = 1;
  private static final String ORGANISATION_UNIT_NAME = "Fivium";
  private static final String ORGANISATION_COMPANY_NUMBER = "15";

  @Mock
  private static OrganisationApi organisationApi;

  @Mock
  private UserService userService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private OrganisationGroupQueryService organisationGroupQueryService;

  @Mock
  private SearchSelectorService searchSelectorService;
  @Mock
  private AreaTeamService areaTeamService;

  private OrganisationUnitQueryService organisationUnitQueryService;

  @BeforeEach
  void setUp() {
    organisationUnitQueryService = new OrganisationUnitQueryService(
        organisationApi,
        new EnergyPortalApiWrapper(serviceConfigurationProperties),
        userService,
        industryTeamService,
        regulatorTeamService,
        organisationGroupQueryService,
        searchSelectorService,
        areaTeamService
    );
  }

  @Test
  void getOrganisationUnits_whenNotRegulator_whenNoOrganisationsFound() {
    var organisationName = "name";
    when(userService.getUser()).thenReturn(USER);
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of());
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(false);

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .isEmpty();
  }

  @Test
  void getOrganisationUnits_whenNotRegulator_whenInTeam_whenNoOrganisationUnitsFound() {
    var organisationName = "name";
    when(userService.getUser()).thenReturn(USER);
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1,2));
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1,2)))
        .thenReturn(List.of());

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .isEmpty();
  }

  @Test
  void getOrganisationUnits_whenRegulator_assertOrganisationFound() {
    var organisationName = "name";
    var organisationUnit = OrganisationUnitTestUtil.Builder().build();
    var filter = OrganisationUnitQueryService.ORGANISATION_UNITS_PROJECTION_ROOT;
    when(userService.getUser()).thenReturn(USER);
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(true);
    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(organisationUnit));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationUnits_whenRegulator_whenDuplicateOrganisations_assertOrganisationFound() {
    var organisationName = "name";
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .build();
    var organisationUnit2 = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .withIsDuplicate(true)
        .build();
    var organisationUnit3 = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .withIsDuplicate(true)
        .build();

    var filter = OrganisationUnitQueryService.ORGANISATION_UNITS_PROJECTION_ROOT;
    when(userService.getUser()).thenReturn(USER);
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(true);
    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(organisationUnit, organisationUnit2, organisationUnit3));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationSearchableResults_whenIndustryOrganisationMember_whenDuplicates_assertOrganisationFound() {
    var organisationName = "name";
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationGroupId(1);
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .withIsDuplicate(false)
        .build();
    var organisationUnit2 = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .withIsDuplicate(true)
        .build();
    organisationGroup.setOrganisationUnits(List.of(organisationUnit, organisationUnit2));
    when(userService.getUser()).thenReturn(USER);
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationSearchableResults_whenIndustryOrganisationMember_assertOrganisationFound() {
    var organisationName = "name";
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationGroupId(1);
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .build();
    organisationGroup.setOrganisationUnits(List.of(organisationUnit));
    when(userService.getUser()).thenReturn(USER);
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationSearchableResults_whenWrongNameAndwhenIndustryOrganisationMember_assertNoOrganisationFound() {
    var organisationName = "name";
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationGroupId(1);
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("wrong")
        .build();
    organisationGroup.setOrganisationUnits(List.of(organisationUnit));
    when(userService.getUser()).thenReturn(USER);
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName))
        .isEmpty();
  }

  @Test
  void findOrganisationUnit_whenNoOrganisationsFound() {
    var organisationId = 1;
    var filter = ORGANISATION_UNIT_PROJECTION_ROOT;
    when(organisationApi.findOrganisationUnit(
        eq(organisationId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.empty());

    assertTrue(organisationUnitQueryService.findOrganisationUnit(organisationId).isEmpty());
  }

  @Test
  void findOrganisationGroupIdsByUnitId_whenNoOrganisationsFound() {
    var organisationId = 1;
    var filter = OrganisationUnitQueryService.ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT;
    when(organisationApi.findOrganisationUnit(
        eq(organisationId),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.empty());

    assertThat(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationId)).isEmpty();
  }

  @Test
  void findOrganisationGroupIdsByUnitId_whenOrganisationsFound() {
    var orgnisationGroupId = 1;
    var orgnisationGroupId2 = 2;

    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationGroupId(orgnisationGroupId);

    var organisationGroup2 = new OrganisationGroup();
    organisationGroup2.setOrganisationGroupId(orgnisationGroupId2);

    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationId(ORGANISATION_UNIT_ID)
        .withOrganisationName(ORGANISATION_UNIT_NAME)
        .withOrganisationNumber(ORGANISATION_COMPANY_NUMBER)
        .withOrganisationGroups(List.of(organisationGroup, organisationGroup2))
        .build();

    var filter = OrganisationUnitQueryService.ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT;

    when(organisationApi.findOrganisationUnit(
        eq(ORGANISATION_UNIT_ID),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));

    var expectedOrganisationGroupIdList =
        organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_UNIT_ID);

    assertThat(expectedOrganisationGroupIdList)
        .containsExactly(1, 2);
  }

  @Test
  void findOrganisationGroupIdsByUnitId_whenOrganisationGroupNotFound() {
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationId(ORGANISATION_UNIT_ID)
        .withOrganisationName(ORGANISATION_UNIT_NAME)
        .withOrganisationNumber(ORGANISATION_COMPANY_NUMBER)
        .withOrganisationGroups(null)
        .build();

    var filter = OrganisationUnitQueryService.ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT;

    when(organisationApi.findOrganisationUnit(
        eq(ORGANISATION_UNIT_ID),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));

    var expectedOrganisationGroupIdList =
        organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_UNIT_ID);

    assertThat(expectedOrganisationGroupIdList).isEmpty();
  }

  @Test
  void findOrganisationUnit_whenOrganisationsFound() {
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationId(ORGANISATION_UNIT_ID)
        .withOrganisationName(ORGANISATION_UNIT_NAME)
        .withOrganisationNumber(ORGANISATION_COMPANY_NUMBER)
        .build();
    var filter = ORGANISATION_UNIT_PROJECTION_ROOT;

    when(organisationApi.findOrganisationUnit(
        eq(ORGANISATION_UNIT_ID),
        eq(filter),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));

    var expectedOrganisationUnit = organisationUnitQueryService.findOrganisationUnit(ORGANISATION_UNIT_ID);
    assertThat(expectedOrganisationUnit)
        .map(OrganisationUnitDto::organisationUnitId)
        .hasValue(ORGANISATION_UNIT_ID);

    assertThat(expectedOrganisationUnit)
        .map(OrganisationUnitDto::companyHouseNumber)
        .hasValue(ORGANISATION_COMPANY_NUMBER);

    assertThat(expectedOrganisationUnit)
        .map(OrganisationUnitDto::organisationUnitName)
        .hasValue(ORGANISATION_UNIT_NAME);
  }

  @Test
  void getPreselectedOrganisation_withNameAndIdOnly() {
    var organisationUnit = new OrganisationUnit();
    organisationUnit.setName("name");
    when(organisationApi.findOrganisationUnit(
        eq(1),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));

    organisationUnitQueryService.getPreSelectedOrganisationUnit(1);

    verify(searchSelectorService).buildPrePopulatedSelection("1", "name");
  }

  @Test
  void getPreselectedOrganisation_withOrganisationNumber() {
    var organisationUnit = new OrganisationUnit();
    organisationUnit.setName("name");
    organisationUnit.setRegisteredNumber("25");
    when(organisationApi.findOrganisationUnit(
        eq(1),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));

    organisationUnitQueryService.getPreSelectedOrganisationUnit(1);

    verify(searchSelectorService).buildPrePopulatedSelection("1", "name (25)");
  }

  @Test
  void getOrganisationUnitNameAndNumberByIdOrEmpty_whenNullId_assertEmpty() {
    var result = organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(null);

    assertThat(result).isEmpty();
  }

  @Test
  void getOrganisationUnitNameAndNumberByIdOrEmpty_whenNotInPortal_assertEmpty() {
    var orgId = 1;
    when(organisationApi.findOrganisationUnit(
        eq(orgId),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.empty());
    var result = organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(orgId);

    assertThat(result).isEmpty();
  }

  @Test
  void getOrganisationUnitNameAndNumberByIdOrEmpty_whenNoNumber_assertName() {
    var orgId = 1;

    when(organisationApi.findOrganisationUnit(
        eq(orgId),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.of(OrganisationUnit.newBuilder()
        .name(ORGANISATION_UNIT_NAME)
        .registeredNumber(null)
        .build()));
    var result = organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(orgId);

    assertThat(result).isEqualTo(ORGANISATION_UNIT_NAME);
  }

  @Test
  void getOrganisationUnitNameAndNumberByIdOrEmpty_assertNameAndNumber() {
    var orgId = 1;

    when(organisationApi.findOrganisationUnit(
        eq(orgId),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.of(OrganisationUnit.newBuilder()
        .registeredNumber(ORGANISATION_COMPANY_NUMBER)
        .name(ORGANISATION_UNIT_NAME).build()));
    var result = organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(orgId);

    assertThat(result).isEqualTo("%s (%s)"
        .formatted(ORGANISATION_UNIT_NAME, ORGANISATION_COMPANY_NUMBER));
  }
}