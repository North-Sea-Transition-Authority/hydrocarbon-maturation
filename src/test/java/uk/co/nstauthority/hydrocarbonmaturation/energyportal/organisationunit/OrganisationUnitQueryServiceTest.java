package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.fivium.energyportalapi.generated.types.OrganisationNameHistory;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.QueryCounter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

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
  private IndustryTeamService industryTeamService;

  @Mock
  private OrganisationGroupQueryService organisationGroupQueryService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private InternalTeamService internalTeamService;

  private Clock clock;

  private OrganisationUnitQueryService organisationUnitQueryService;

  @BeforeEach
  void setUp() {
    var queryCounter = mock(QueryCounter.class);
    organisationUnitQueryService = new OrganisationUnitQueryService(
        organisationApi,
        new EnergyPortalApiWrapper(serviceConfigurationProperties, queryCounter),
        organisationGroupQueryService,
        searchSelectorService,
        internalTeamService,
        industryTeamService
    );
    clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
  }

  @Test
  void getOrganisationUnits_whenNotRegulator_whenNoOrganisationsFound() {
    var organisationName = "name";
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of());
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .isEmpty();
  }

  @Test
  void getOrganisationUnits_whenNotRegulator_whenInTeam_whenNoOrganisationUnitsFound() {
    var organisationName = "name";
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1,2));
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1,2)))
        .thenReturn(List.of());

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .isEmpty();
  }

  @Test
  void getOrganisationUnits_whenRegulator_assertOrganisationFound() {
    var organisationName = "name";
    var organisationUnit = OrganisationUnitTestUtil.Builder().build();
    var filter = OrganisationUnitQueryService.ORGANISATION_UNITS_PROJECTION_ROOT;
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(organisationUnit));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::organisationNameHistoryDtoList,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                Collections.emptyList(),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationUnits_whenRegulator_andOrganisationHasNameHistory_assertOrganisationFound() {
    var organisationName = "name";
    var orgNameHistory = new OrganisationNameHistory();
    orgNameHistory.setName("old name");
    var startDate = LocalDate.ofInstant(clock.instant(), clock.getZone());
    var endDate = LocalDate.ofInstant(clock.instant().plus(30, ChronoUnit.DAYS), clock.getZone());
    orgNameHistory.setStartDate(startDate);
    orgNameHistory.setEndDate(endDate);
    var orgNameHistoryList = List.of(orgNameHistory);
    var orgNameHistoryDto = new OrganisationNameHistoryDto(
        orgNameHistory.getName(),
        orgNameHistory.getStartDate(),
        orgNameHistory.getEndDate()
    );
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .withOrganisationNameHistory(orgNameHistoryList)
        .build();
    var filter = OrganisationUnitQueryService.ORGANISATION_UNITS_PROJECTION_ROOT;
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(organisationUnit));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::organisationNameHistoryDtoList,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                List.of(orgNameHistoryDto),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationUnitsWithDate_whenRegulator_assertOrganisationFound() {
    var submissionDetail = new SubmissionDetail();
    var acceptedTimestamp = Instant.now();
    var acceptedLocalDate = DateUtil.createLocalDate(acceptedTimestamp);
    submissionDetail.setAcceptedTimestamp(acceptedTimestamp);
    var organisationName = "name";
    List<OrganisationNameHistory> organisationNameHistoryList = new ArrayList<>();
    var organisationNameHistory = new OrganisationNameHistory();
    organisationNameHistory.setName(organisationName);
    organisationNameHistory.setStartDate(LocalDate.of(2001, 1, 1));
    organisationNameHistory.setEndDate(null);
    organisationNameHistoryList.add(organisationNameHistory);
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName(organisationName)
        .withOrganisationNameHistory(organisationNameHistoryList)
        .build();
    var filter = OrganisationUnitQueryService.ORGANISATION_UNITS_PROJECTION_ROOT;
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(organisationUnit));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER, submissionDetail))
        .extracting(
            OrganisationUnitWithDateDto::organisationUnitId,
            OrganisationUnitWithDateDto::organisationUnitName,
            OrganisationUnitWithDateDto::companyHouseNumber,
            OrganisationUnitWithDateDto::organisationNameHistoryDtoList,
            OrganisationUnitWithDateDto::getSelectionText,
            OrganisationUnitWithDateDto::getSelectionId,
            OrganisationUnitWithDateDto::organisationNameDate
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                organisationUnit.getOrganisationNameHistory()
                    .stream()
                    .map(OrganisationNameHistoryDto::fromOrganisationNameHistory)
                    .toList(),
                "%s (%s)".formatted(
                    organisationUnit.getOrganisationNameHistory().get(0).getName(),
                    organisationUnit.getRegisteredNumber()
                ),
                organisationUnit.getOrganisationUnitId().toString(),
                acceptedLocalDate
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
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(organisationApi.searchOrganisationUnits(
        eq(organisationName),
        eq(filter),
        any(),
        any()
    )).thenReturn(List.of(organisationUnit, organisationUnit2, organisationUnit3));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::organisationNameHistoryDtoList,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                Collections.emptyList(),
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
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::organisationNameHistoryDtoList,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                Collections.emptyList(),
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
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::organisationNameHistoryDtoList,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                Collections.emptyList(),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationSearchableResults_whenIndustryOrganisationMember_andOrganisationNameHistoryExists_assertOrganisationFound() {
    var organisationName = "name";
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationGroupId(1);
    var orgNameHistory = new OrganisationNameHistory();
    orgNameHistory.setName("old name");
    var startDate = LocalDate.ofInstant(clock.instant(), clock.getZone());
    var endDate = LocalDate.ofInstant(clock.instant().plus(30, ChronoUnit.DAYS), clock.getZone());
    orgNameHistory.setStartDate(startDate);
    orgNameHistory.setEndDate(endDate);
    var orgNameHistoryList = List.of(orgNameHistory);
    var orgNameHistoryDto = new OrganisationNameHistoryDto(
        orgNameHistory.getName(),
        orgNameHistory.getStartDate(),
        orgNameHistory.getEndDate()
    );
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .withOrganisationNameHistory(orgNameHistoryList)
        .build();
    organisationGroup.setOrganisationUnits(List.of(organisationUnit));
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
        .extracting(
            OrganisationUnitDto::organisationUnitId,
            OrganisationUnitDto::organisationUnitName,
            OrganisationUnitDto::companyHouseNumber,
            OrganisationUnitDto::organisationNameHistoryDtoList,
            OrganisationUnitDto::getSelectionText,
            OrganisationUnitDto::getSelectionId
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                List.of(orgNameHistoryDto),
                "%s (%s)".formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString()
            )
        );
  }

  @Test
  void getOrganisationSearchableResultsWithSubmissionDetail_whenIndustryOrganisationMember_andOrganisationNameHistoryExists_assertOrganisationFound() {
    var organisationName = "name";
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationGroupId(1);
    var orgNameHistory = new OrganisationNameHistory();
    orgNameHistory.setName("old name");
    var startDate = LocalDate.ofInstant(clock.instant(), clock.getZone());
    var endDate = LocalDate.ofInstant(clock.instant().plus(30, ChronoUnit.DAYS), clock.getZone());
    orgNameHistory.setStartDate(startDate);
    orgNameHistory.setEndDate(endDate);
    var orgNameHistoryList = List.of(orgNameHistory);
    var orgNameHistoryDto = new OrganisationNameHistoryDto(
        orgNameHistory.getName(),
        orgNameHistory.getStartDate(),
        orgNameHistory.getEndDate()
    );
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("name")
        .withOrganisationNameHistory(orgNameHistoryList)
        .build();
    organisationGroup.setOrganisationUnits(List.of(organisationUnit));
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));
    var submissionDetail = new SubmissionDetail();
    var acceptedTimestamp = Instant.now();
    var acceptedLocalDate = DateUtil.createLocalDate(acceptedTimestamp);
    submissionDetail.setAcceptedTimestamp(acceptedTimestamp);

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER, submissionDetail))
        .extracting(
            OrganisationUnitWithDateDto::organisationUnitId,
            OrganisationUnitWithDateDto::organisationUnitName,
            OrganisationUnitWithDateDto::companyHouseNumber,
            OrganisationUnitWithDateDto::organisationNameHistoryDtoList,
            OrganisationUnitWithDateDto::getSelectionText,
            OrganisationUnitWithDateDto::getSelectionId,
            OrganisationUnitWithDateDto::organisationNameDate
        ).containsExactly(
            tuple(
                organisationUnit.getOrganisationUnitId(),
                organisationUnit.getName(),
                organisationUnit.getRegisteredNumber(),
                List.of(orgNameHistoryDto),
                "%s (%s)".formatted(orgNameHistoryDto.name(), organisationUnit.getRegisteredNumber()),
                organisationUnit.getOrganisationUnitId().toString(),
                acceptedLocalDate
            )
        );
  }

  @Test
  void getOrganisationSearchableResults_whenWrongNameAndIndustryOrganisationMember_assertNoOrganisationFound() {
    var organisationName = "name";
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationGroupId(1);
    var organisationUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationName("wrong")
        .build();
    organisationGroup.setOrganisationUnits(List.of(organisationUnit));
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(1));
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(1)))
        .thenReturn(List.of(organisationGroup));

    assertThat(organisationUnitQueryService.getOrganisationSearchableResults(organisationName, USER))
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
  void getPreselectedOrganisationForDate_withNoExistingOrganisation() {
    var resourceDetail = new ResourceDetail();
    var acceptedDate = LocalDate.now();

    assertThat(organisationUnitQueryService.getPreSelectedOrganisationUnitForDate(resourceDetail, acceptedDate)).isEmpty();
  }

  @Test
  void getPreselectedOrganisationForDate_withOrganisationNotFound() {
    var resourceDetail = new ResourceDetail();
    var organisationId = 1;
    resourceDetail.setOrganisationId(organisationId);
    var acceptedDate = LocalDate.now();


    when(organisationApi.findOrganisationUnit(
        eq(organisationId),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.empty());

    assertThat(organisationUnitQueryService.getPreSelectedOrganisationUnitForDate(resourceDetail, acceptedDate)).isEmpty();
  }

  @Test
  void getPreselectedOrganisationForDate_withIdAndRegisteredNumber() {
    var resourceDetail = new ResourceDetail();
    var organisationId = 1;
    var oldOrganisationName = "old name";
    var currentOrganisationName = "new name";
    var organisationRegisteredNumber = "0000999";
    resourceDetail.setOrganisationId(organisationId);
    resourceDetail.setOrganisationRegisteredNumber(organisationRegisteredNumber);
    List<OrganisationNameHistory> nameHistory = new ArrayList<>();
    var organisationNameHistory1 = new OrganisationNameHistory(
        oldOrganisationName,
        LocalDate.of(2012, 1, 1),
        LocalDate.of(2013, 1, 1)
    );
    var organisationNameHistory2 = new OrganisationNameHistory(
        currentOrganisationName,
        LocalDate.of(2013, 1, 2),
        LocalDate.of(2014, 1, 1)
    );
    nameHistory.add(organisationNameHistory1);
    nameHistory.add(organisationNameHistory2);

    var organisationUnit = new OrganisationUnit();
    organisationUnit.setName(currentOrganisationName);
    organisationUnit.setRegisteredNumber(organisationRegisteredNumber);
    organisationUnit.setOrganisationNameHistory(nameHistory);
    when(organisationApi.findOrganisationUnit(
        eq(organisationId),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));


    var acceptedDate = LocalDate.of(2012, 6, 1);

    organisationUnitQueryService.getPreSelectedOrganisationUnitForDate(resourceDetail, acceptedDate);

    verify(searchSelectorService).buildPrePopulatedSelection("1", "old name (0000999)");
  }

  @Test
  void getPreselectedOrganisationForDate_withoutRegisteredNumber() {
    var resourceDetail = new ResourceDetail();
    var organisationId = 1;
    var oldOrganisationName = "old name";
    var currentOrganisationName = "new name";
    resourceDetail.setOrganisationId(organisationId);
    List<OrganisationNameHistory> nameHistory = new ArrayList<>();
    var organisationNameHistory1 = new OrganisationNameHistory(
        oldOrganisationName,
        LocalDate.of(2012, 1, 1),
        LocalDate.of(2013, 1, 1)
    );
    var organisationNameHistory2 = new OrganisationNameHistory(
        currentOrganisationName,
        LocalDate.of(2013, 1, 2),
        LocalDate.of(2014, 1, 1)
    );
    nameHistory.add(organisationNameHistory1);
    nameHistory.add(organisationNameHistory2);

    var organisationUnit = new OrganisationUnit();
    organisationUnit.setName(currentOrganisationName);
    organisationUnit.setOrganisationNameHistory(nameHistory);
    when(organisationApi.findOrganisationUnit(
        eq(organisationId),
        eq(OrganisationUnitQueryService.ORGANISATION_UNIT_PROJECTION_ROOT),
        any(),
        any()
    )).thenReturn(Optional.of(organisationUnit));


    var acceptedDate = LocalDate.of(2012, 6, 1);

    organisationUnitQueryService.getPreSelectedOrganisationUnitForDate(resourceDetail, acceptedDate);

    verify(searchSelectorService).buildPrePopulatedSelection("1", "old name");
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

  @Test
  void getOrganisationUnitNameForDate() {
    var oldOrganisationName = "old name";
    var currentOrganisationName = "new name";
    var organisationUnitId = 1;

    var nameHistory = getOrganisationNameHistoryDtos(oldOrganisationName, currentOrganisationName);

    var organisationUnit = new OrganisationUnitDto(
        organisationUnitId,
        currentOrganisationName,
        "00009999",
        nameHistory
    );

    var acceptedDate = LocalDate.of(2012, 6, 1);
    var orgNameAtDate = organisationUnitQueryService.getOrganisationUnitNameForDate(organisationUnit, acceptedDate);

    assertThat(orgNameAtDate).isPresent();
    assertEquals(oldOrganisationName, orgNameAtDate.get());
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