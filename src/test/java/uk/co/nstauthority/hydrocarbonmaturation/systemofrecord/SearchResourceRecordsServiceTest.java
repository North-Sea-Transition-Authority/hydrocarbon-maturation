package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchResourceRecordsService.MAX_RESULTS;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorAndLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@ExtendWith(MockitoExtension.class)
class SearchResourceRecordsServiceTest {

  private static final User USER = UserTestUtil.Builder().build();
  private static final LicenceDto EXTANT_LICENCE_1 = buildLicenceView(1, "L1", LicenceStatus.EXTANT);
  private static final LicenceDto EXTANT_LICENCE_2 = buildLicenceView(2, "L2", LicenceStatus.EXTANT);
  private static final LicenceDto NOT_EXTANT_LICENCE_2 = buildLicenceView(3, "F1", LicenceStatus.EXPIRED);
  private static final LicenceDto NOT_EXTANT_LICENCE_3 = buildLicenceView(4, "F2", LicenceStatus.EXPIRED);
  private static final Map<Integer, LicenceDto> LICENCE_MAP = Map.of(
      1, EXTANT_LICENCE_1,
      2, EXTANT_LICENCE_2,
      3, NOT_EXTANT_LICENCE_2,
      4, NOT_EXTANT_LICENCE_3
  );
  private static final int ORGANISATION_ID_1 = 1;
  private static final int ORGANISATION_ID_2 = 2;
  private static final OrganisationUnitDto ORGANISATION_UNIT_1 = new OrganisationUnitDto(ORGANISATION_ID_1, "Org A", null, Collections.emptyList());
  private static final OrganisationUnitDto ORGANISATION_UNIT_2 = new OrganisationUnitDto(ORGANISATION_ID_2, "Org B", null, Collections.emptyList());
  private static final Map<Integer, OrganisationUnitDto> ORGANISATION_UNIT_MAP = Map.of(
      ORGANISATION_ID_1, ORGANISATION_UNIT_1,
      ORGANISATION_ID_2, ORGANISATION_UNIT_2
  );

  @Mock
  private ResourceRecordQueryServiceStrategy resourceRecordQueryServiceStrategy;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @Mock
  private SearchSelectorService searchSelectorService;
  @Mock
  private LicenceQueryService licenceQueryService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @Mock
  private FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService;
  @Mock
  private InternalTeamService internalTeamService;
  @Mock
  private IndustryTeamService industryTeamService;
  @InjectMocks
  private SearchResourceRecordsService underTest;
  private final Instant fixedInstant = Instant.parse("2002-01-11T12:00:00.00Z");

  @Test
  void getOpportunityTypes() {
    assertThat(underTest.getOpportunityTypes())
        .containsExactly(
            entry(OpportunityType.LEAD.name(), "Lead"),
            entry(OpportunityType.PROSPECT.name(), "Prospect"),
            entry(OpportunityType.DRILL_READY_PROSPECT.name(), "Drill-ready prospect"),
            entry(OpportunityType.DRY_WELL.name(), "Dry-well"),
            entry(OpportunityType.DISCOVERY.name(), "Discovery"),
            entry(OpportunityType.OTHER.name(), "Other"),
            entry("NOT_SET", "Not set")
        );
  }

  @Test
  void getResourceTypes() {
    assertThat(underTest.getResourceTypes())
        .containsExactly(
            entry(ResourceType.PROSPECT.name(), "Prospect"),
            entry(ResourceType.DISCOVERY.name(), "Discovery")
        );
  }

  @Nested
  class WhenGettingResourceRecordSearchResults {

    @Mock
    private SearchResourceRecordsForm form;

    @BeforeEach
    void setUp() {
      when(organisationUnitQueryService.getOrganisationUnitMapByIds(anyList())).thenReturn(ORGANISATION_UNIT_MAP);
      when(licenceQueryService.getLicenceMapByIds(anyList())).thenReturn(LICENCE_MAP);
    }

    @Test
    void buildsDiscoveryAndProspectRecordSearchResultItems_asRegulator() {
      var orgName1 = "Org A";
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, ORGANISATION_ID_1))
          .thenReturn(orgName1);
      var orgName2 = "Org B";
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, ORGANISATION_ID_2))
          .thenReturn(orgName2);

      var result1 = buildAndMockResourceRecordQueryResult(
          "resource-name1",
          ResourceType.PROSPECT,
          null,
          "1/12",
          ORGANISATION_ID_1,
          List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
          ResponsibleTeamType.EXPLORATION,
          null,
          null,
          null,
          SubmissionType.CREATE_PROSPECT,
          true
      );
      var result2 = buildAndMockResourceRecordQueryResult(
          "resource-name2",
          ResourceType.DISCOVERY,
          null,
          null,
          ORGANISATION_ID_2,
          List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId()),
          ResponsibleTeamType.EXPLORATION,
          null,
          null,
          fixedInstant,
          SubmissionType.RELINQUISHMENT_DISCOVERY,
          true
      );
      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2));
      var licencesTheResourceIsOn1 = List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId());
      when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, licencesTheResourceIsOn1))
          .thenReturn(List.of(EXTANT_LICENCE_1.licenceRef(), EXTANT_LICENCE_2.licenceRef()));
      var licencesTheResourceIsOn2 = List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId());
      when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, licencesTheResourceIsOn2))
          .thenReturn(List.of(NOT_EXTANT_LICENCE_2.licenceRef(), NOT_EXTANT_LICENCE_3.licenceRef()));

      var results = underTest.getResourceRecordSearchResults(form, resourceRecordQueryServiceStrategy, USER);

      assertThat(results)
          .extracting(
              ResourceRecordSearchResultItem::headingText,
              ResourceRecordSearchResultItem::captionText,
              ResourceRecordSearchResultItem::actionUrl
          )
          .containsExactly(
              tuple(
                  "resource-name1 (1/12)",
                  result1.resourceType().getDisplayName(),
                  getActionUrl(result1.resourceId())
              ),
              tuple(
                  "resource-name2",
                  "%s (Relinquishment): latest submission on 11 January 2002"
                      .formatted(result2.resourceType().getDisplayName()),
                  getActionUrl(result2.resourceId())
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.EXPLORATION.getDisplayName()),
              entry("organisationResponsible", orgName1),
              entry("isArchived", false),
              entry("licences", List.of("L1", "L2"))
          );

      assertThat(results.get(1).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.EXPLORATION.getDisplayName()),
              entry("organisationResponsible", orgName2),
              entry("isArchived", false),
              entry("licences", List.of("F1", "F2"))
          );
    }

    @Test
    void buildsDiscoveryAndProspectRecordSearchResultItems_withMatchingOrganisationsAndSomeExtantLicences() {
      var orgName1 = "Org A";
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, ORGANISATION_ID_1))
          .thenReturn(orgName1);

      var result1 = buildAndMockResourceRecordQueryResult(
        "resource-name1",
        ResourceType.PROSPECT,
        null,
        "1/12",
        ORGANISATION_ID_1,
        List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
        ResponsibleTeamType.EXPLORATION,
        null,
        null,
        null,
        SubmissionType.CREATE_PROSPECT,
        true
      );
      var result2 = buildAndMockResourceRecordQueryResult(
        "resource-name2",
        ResourceType.DISCOVERY,
        null,
        null,
        ORGANISATION_ID_2,
        List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId()),
        ResponsibleTeamType.EXPLORATION,
        null,
        null,
        fixedInstant,
        SubmissionType.RELINQUISHMENT_PROSPECT,
        true
      );
      var organisationUnitList = List.of(ORGANISATION_ID_1, ORGANISATION_ID_2);
      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(
          USER, Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER)
      )).thenReturn(organisationUnitList);
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2));
      var licencesTheResourceIsOn = List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId());
      when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, licencesTheResourceIsOn))
          .thenReturn(List.of(EXTANT_LICENCE_1.licenceRef(), EXTANT_LICENCE_2.licenceRef()));

      var results = underTest.getResourceRecordSearchResults(form, resourceRecordQueryServiceStrategy, USER);

      assertThat(results)
          .extracting(
              ResourceRecordSearchResultItem::headingText,
              ResourceRecordSearchResultItem::captionText,
              ResourceRecordSearchResultItem::actionUrl
          )
          .containsExactly(
              tuple(
                  "resource-name1 (1/12)",
                  result1.resourceType().getDisplayName(),
                  getActionUrl(result1.resourceId())
              )
          );

      assertThat(results.getFirst().modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.EXPLORATION.getDisplayName()),
              entry("organisationResponsible", orgName1),
              entry("isArchived", false),
              entry("licences", List.of("L1", "L2"))
          );
    }

    @Test
    void buildsFieldRecordSearchResultItem_asRegulator() {
      var orgName1 = "Org A";

      var fieldId1 = 1;
      var fieldId2 = 2;
      var fieldId3 = 3;
      var result1 = buildAndMockResourceRecordQueryResult(
          "field-name1",
          ResourceType.FIELD,
          fieldId1,
          "1/12",
          ORGANISATION_ID_1,
          null,
          ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
          null,
          FieldStatus.STATUS_200,
          fixedInstant,
          SubmissionType.CREATE_CSR,
          true
      );
      var result2 = buildAndMockResourceRecordQueryResult(
          "field-name2",
          ResourceType.FIELD,
          fieldId2,
          null,
          ORGANISATION_ID_2,
          List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId()),
          ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
          "Parent field name",
          FieldStatus.STATUS_400,
          fixedInstant,
          SubmissionType.CREATE_FDP,
          true
      );
      var result3 = buildAndMockResourceRecordQueryResult(
          "field-name3",
          ResourceType.FIELD,
          fieldId3,
          null,
          ORGANISATION_ID_2,
          List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
          ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
          "Parent field name",
          FieldStatus.STATUS_400,
          null,
          null,
          true
      );

      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2, result3));

      var field1 = new Field();
      field1.setFieldId(fieldId1);
      var organisationUnit = new OrganisationUnit();
      organisationUnit.setOrganisationUnitId(ORGANISATION_ID_1);
      organisationUnit.setName(orgName1);
      field1.setFieldOperator(organisationUnit);
      var licence1 = new Licence();
      licence1.setId(EXTANT_LICENCE_1.licenceId());
      licence1.setLicenceRef(EXTANT_LICENCE_1.licenceRef());
      licence1.setLicenceStatus(EXTANT_LICENCE_1.licenceStatus());
      field1.setLicences(List.of(licence1));
      var field2 = new Field();
      field2.setFieldId(fieldId2);
      field2.setFieldOperator(organisationUnit);
      var licence2 = new Licence();
      licence2.setId(NOT_EXTANT_LICENCE_2.licenceId());
      licence2.setLicenceRef(NOT_EXTANT_LICENCE_2.licenceRef());
      licence2.setLicenceStatus(NOT_EXTANT_LICENCE_2.licenceStatus());
      field2.setLicences(List.of(licence2));
      var field3 = new Field();
      field3.setFieldId(fieldId3);
      field3.setFieldOperator(organisationUnit);
      field3.setLicences(Collections.emptyList());
      when(fieldOperatorAndLicenceQueryService.getFieldsByIds(List.of(fieldId1, fieldId2, fieldId3)))
          .thenReturn(List.of(field1, field2, field3));

      when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(ORGANISATION_UNIT_1))
          .thenReturn(orgName1);

      var results = underTest.getResourceRecordSearchResults(form, resourceRecordQueryServiceStrategy, USER);

      assertThat(results)
          .extracting(
              ResourceRecordSearchResultItem::headingText,
              ResourceRecordSearchResultItem::captionText,
              ResourceRecordSearchResultItem::actionUrl
          )
          .containsExactly(
              tuple(
                  "field-name1 (1/12)",
                  "Field: latest submission on 11 January 2002",
                  getActionUrl(result1.resourceId())
              ),
              tuple(
                  "field-name2",
                  "Field: latest submission on 11 January 2002",
                  getActionUrl(result2.resourceId())
              ),
              tuple(
                  "field-name3",
                  "Field",
                  getActionUrl(result3.resourceId())
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND.getDisplayName()),
              entry("organisationResponsible", orgName1),
              entry("isArchived", false),
              entry("fieldStatus", result1.fieldStatus().getDisplayName()),
              entry("parentField", "No parent field")
          );

      assertThat(results.get(1).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getDisplayName()),
              entry("organisationResponsible", orgName1),
              entry("isArchived", false),
              entry("fieldStatus", result2.fieldStatus().getDisplayName()),
              entry("parentField", "Parent field name")
          );

      assertThat(results.get(2).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getDisplayName()),
              entry("organisationResponsible", orgName1),
              entry("isArchived", false),
              entry("fieldStatus", result3.fieldStatus().getDisplayName()),
              entry("parentField", "Parent field name")
          );
    }

    @Test
    void buildsFieldRecordSearchResultItem_withMatchingOrganisationsAndSomeExtantLicences() {
      var orgName1 = "Org A";

      var fieldId1 = 1;
      var fieldId2 = 2;
      var fieldId3 = 3;
      var result1 = buildAndMockResourceRecordQueryResult(
        "field-name1",
        ResourceType.FIELD,
        fieldId1,
        "1/12",
        ORGANISATION_ID_1,
        null,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        null,
        FieldStatus.STATUS_200,
        fixedInstant,
        SubmissionType.CREATE_CSR,
        true
      );
      var result2 = buildAndMockResourceRecordQueryResult(
        "field-name2",
        ResourceType.FIELD,
        fieldId2,
        null,
        ORGANISATION_ID_2,
        List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId()),
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
        "Parent field name",
        FieldStatus.STATUS_400,
        fixedInstant,
        SubmissionType.CREATE_FDP,
        true
      );
      var result3 = buildAndMockResourceRecordQueryResult(
          "field-name3",
          ResourceType.FIELD,
          fieldId3,
          null,
          ORGANISATION_ID_2,
          List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
          ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
          "Parent field name",
          FieldStatus.STATUS_400,
          null,
          null,
          true
      );

      var organisationUnitList = List.of(ORGANISATION_ID_1, ORGANISATION_ID_2);
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(
          USER, Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER)
      )).thenReturn(organisationUnitList);
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2, result3));

      var field1 = new Field();
      field1.setFieldId(fieldId1);
      var organisationUnit = new OrganisationUnit();
      organisationUnit.setOrganisationUnitId(ORGANISATION_ID_1);
      organisationUnit.setName(orgName1);
      field1.setFieldOperator(organisationUnit);
      var licence1 = new Licence();
      licence1.setId(EXTANT_LICENCE_1.licenceId());
      licence1.setLicenceRef(EXTANT_LICENCE_1.licenceRef());
      licence1.setLicenceStatus(EXTANT_LICENCE_1.licenceStatus());
      field1.setLicences(List.of(licence1));
      var field2 = new Field();
      field2.setFieldId(fieldId2);
      field2.setFieldOperator(organisationUnit);
      var licence2 = new Licence();
      licence2.setId(NOT_EXTANT_LICENCE_2.licenceId());
      licence2.setLicenceRef(NOT_EXTANT_LICENCE_2.licenceRef());
      licence2.setLicenceStatus(NOT_EXTANT_LICENCE_2.licenceStatus());
      field2.setLicences(List.of(licence2));
      var field3 = new Field();
      field3.setFieldId(fieldId3);
      field3.setFieldOperator(organisationUnit);
      field3.setLicences(Collections.emptyList());
      when(fieldOperatorAndLicenceQueryService.getFieldsByIds(List.of(fieldId1, fieldId2, fieldId3)))
          .thenReturn(List.of(field1, field2, field3));

      when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(ORGANISATION_UNIT_1))
          .thenReturn(orgName1);

      var results = underTest.getResourceRecordSearchResults(form, resourceRecordQueryServiceStrategy, USER);

      assertThat(results)
          .extracting(
              ResourceRecordSearchResultItem::headingText,
              ResourceRecordSearchResultItem::captionText,
              ResourceRecordSearchResultItem::actionUrl
          )
          .containsExactly(
              tuple(
                  "field-name1 (1/12)",
                  "Field: latest submission on 11 January 2002",
                  getActionUrl(result1.resourceId())
              ),
              tuple(
                  "field-name3",
                  "Field",
                  getActionUrl(result3.resourceId())
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND.getDisplayName()),
              entry("organisationResponsible", orgName1),
              entry("isArchived", false),
              entry("fieldStatus", result1.fieldStatus().getDisplayName()),
              entry("parentField", "No parent field")
          );

      assertThat(results.get(1).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getDisplayName()),
              entry("organisationResponsible", orgName1),
              entry("isArchived", false),
              entry("fieldStatus", result3.fieldStatus().getDisplayName()),
              entry("parentField", "Parent field name")
          );
    }

    private String getActionUrl(UUID resourceId) {
      return ReverseRouter.route(on(ViewResourceRecordController.class)
          .showResourceRecord(resourceId, null));
    }

    private ResourceRecordQueryResult buildAndMockResourceRecordQueryResult(String resourceName,
                                                                            ResourceType resourceType,
                                                                            Integer fieldIdentifier,
                                                                            String centreOfInterest,
                                                                            int organisationResponsibleId,
                                                                            List<Integer> licencesTheResourceIsOn,
                                                                            ResponsibleTeamType responsibleTeam,
                                                                            String parentFieldName,
                                                                            FieldStatus fieldStatus,
                                                                            Instant acceptedSubmissionInstant,
                                                                            SubmissionType submissionType,
                                                                            boolean active) {
      when(submissionDetailDisplayService.getTeamResponsibleDisplay(responsibleTeam)).thenReturn(responsibleTeam.getDisplayName());

      return new ResourceRecordQueryResult(
          UUID.randomUUID(),
          UUID.randomUUID(),
          acceptedSubmissionInstant,
          submissionType,
          resourceType,
          fieldIdentifier,
          resourceName,
          centreOfInterest,
          organisationResponsibleId,
          licencesTheResourceIsOn,
          responsibleTeam,
          parentFieldName == null ? "No parent field" : parentFieldName,
          fieldStatus,
          null,
          active);
    }

  }

  private static LicenceDto buildLicenceView(int id, String ref, LicenceStatus status) {
    return LicenceViewTestUtil.Builder()
        .withLicenceId(id)
        .withLicenceRef(ref)
        .withLicenceStatus(status)
        .build();
  }

  @Test
  void maxResultsReturned_isAtLimit() {
    Supplier<ResourceRecordSearchResultItem> result = ResourceRecordSearchResultItem::empty;
    var results = Stream.generate(result).limit(MAX_RESULTS).toList();
    assertThat(underTest.maxResultsReturned(results)).isTrue();
  }

  @Test
  void maxResultsReturned_isNotAtLimit() {
    Supplier<ResourceRecordSearchResultItem> result = ResourceRecordSearchResultItem::empty;
    var results = Stream.generate(result).limit(MAX_RESULTS - 1).toList();
    assertThat(underTest.maxResultsReturned(results)).isFalse();
  }

  @Nested
  class WhenGettingResourceRecordSearchResultsDistinctNames {

    @Test
    void getResourceRecordSearchResultsDistinctResourceNames() {
      var licenceId = 100;
      var licenceDto = new LicenceDto("L100", licenceId, LicenceStatus.EXTANT);
      var result1 = newSearchResult("Result Z", ResourceType.PROSPECT, null, null, null);
      var result2 = newSearchResult("Result A", ResourceType.DISCOVERY, null, ORGANISATION_ID_1, List.of(licenceId));
      var fieldId1 = 10;
      var result3 = newSearchResult("Result b", ResourceType.FIELD, fieldId1, null, null);
      var searchResults = List.of(result1, result2, result3);

      var form = new SearchResourceRecordsForm();

      var queryServiceStrategy = mock(ResourceRecordQueryServiceStrategy.class);
      when(queryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(searchResults);

      var expectedResult = Stream.of(result3, result2)
          .map(ResourceRecordQueryResult::resourceName).toList();

      var organisationUnitList = List.of(ORGANISATION_ID_1, ORGANISATION_ID_2);
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(
          USER, Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER)
      )).thenReturn(organisationUnitList);


      var field1 = new Field();
      field1.setFieldId(fieldId1);
      var organisationUnit = new OrganisationUnit();
      organisationUnit.setOrganisationUnitId(ORGANISATION_ID_1);
      field1.setFieldOperator(organisationUnit);
      var licence1 = new Licence();
      licence1.setId(99);
      licence1.setLicenceRef("L99");
      licence1.setLicenceStatus(LicenceStatus.EXTANT);
      field1.setLicences(List.of(licence1));
      when(fieldOperatorAndLicenceQueryService.getFieldsByIds(List.of(fieldId1)))
          .thenReturn(List.of(field1));
      when(licenceQueryService.getLicenceMapByIds(List.of(licenceId))).thenReturn(Map.of(licenceId, licenceDto));

      assertThat(underTest.getResourceRecordSearchResultsDistinctResourceNames(form, queryServiceStrategy, USER))
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    private ResourceRecordQueryResult newSearchResult(String resourceName,
                                                      ResourceType resourceType,
                                                      Integer fieldIdentifier,
                                                      Integer organisationId,
                                                      List<Integer> licencesTheResourceIsOn) {
      return new ResourceRecordQueryResult(null, null, null, null,resourceType, fieldIdentifier,
          resourceName, null, organisationId, licencesTheResourceIsOn, null, null, null, null, false);
    }
  }

  @Nested
  class WhenGettingPreSelectedResourceName {
    private SearchResourceRecordsForm form;

    @BeforeEach
    void setup() {
      form = new SearchResourceRecordsForm();
    }

    @Test
    void getPreSelectedResourceName_noResourceName_returnsEmptyMap() {
      var result = underTest.getPreSelectedResourceName(form);

      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void getPreSelectedResourceName_returnsMapOfResourceName() {
      var resourceName = "Resource Name";
      form.setResourceNameFromResourceDetail(resourceName);

      var prePopulatedSelection = mock(Map.class);
      when(searchSelectorService.buildPrePopulatedSelection(resourceName))
          .thenReturn(prePopulatedSelection);

      var result = underTest.getPreSelectedResourceName(form);
      assertThat(result).isEqualTo(prePopulatedSelection);

      verify(searchSelectorService).buildPrePopulatedSelection(
          resourceName
      );
    }

    @Test
    void fieldResource_noFieldName() {
      form.setFieldNameForResourceSubmission(null);

      var result = underTest.getPreSelectedResourceName(form);
      assertThat(result).isEqualTo(Map.of());

      verifyNoInteractions(searchSelectorService);
    }

    @Test
    void fieldResource_VerifyCalls() {
      var fieldName = "Field Name";
      form.setFieldNameForResourceSubmission(fieldName);

      var prePopulatedSelection = mock(Map.class);
      when(searchSelectorService.buildPrePopulatedSelection(fieldName))
          .thenReturn(prePopulatedSelection);

      var result = underTest.getPreSelectedFieldName(form);

      assertThat(result).isEqualTo(prePopulatedSelection);

      verify(searchSelectorService).buildPrePopulatedSelection(fieldName);
    }
  }

  @Nested
  class WhenGettingPreSelectedOrganisation {

    private SearchResourceRecordsForm form;

    @BeforeEach
    void setup() {
      form = new SearchResourceRecordsForm();
    }

    @Test
    void noSelectedOrganisation_ReturnsEmptyMap() {
      var result = underTest.getPreSelectedOrganisationUnit(form);

      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void preSelectedResourceName_VerifyCalls() {
      var responsibleOrganisationId = 1;
      form.setResponsibleOrganisationId(responsibleOrganisationId);

      underTest.getPreSelectedOrganisationUnit(form);

      verify(organisationUnitQueryService).getPreSelectedOrganisationUnit(responsibleOrganisationId);
    }

    @Test
    void getPreselectedLicence_whenEmptyForm_ReturnsEmptyMap() {
      var result = underTest.getPreselectedLicence(form);
      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void getPreselectedLicence_whenSelectedLicence_ReturnsMap() {
      var id = "1";
      form.setLicenceId(id);
      var expectedMap = Map.of("key", "value");
      var licenceView = new LicenceDto("ref", Integer.parseInt(id), LicenceStatus.EXTANT);
      when(licenceQueryService.getLicenceById(Integer.valueOf(id)))
          .thenReturn(Optional.of(licenceView));
      when(searchSelectorService.buildPrePopulatedSelection(id, "ref"))
          .thenReturn(expectedMap);

      var result = underTest.getPreselectedLicence(form);
      assertThat(result).isEqualTo(expectedMap);
    }
  }

  @Nested
  class WhenGettingPreSelectedQuadBlock {

    private SearchResourceRecordsForm form;

    @BeforeEach
    void setup() {
      form = new SearchResourceRecordsForm();
    }

    @Test
    void noSelectedQuadBlock_ReturnsEmptyMap() {
      var result = underTest.getPreSelectedQuadAndBlock(form);

      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void preSelectedQuadBlock_VerifyCalls() {
      var quadBlockSelected = "15/10";
      form.setCentreOfInterestBlock(quadBlockSelected);

      underTest.getPreSelectedQuadAndBlock(form);

      verify(searchSelectorService).buildPrePopulatedSelection(quadBlockSelected, quadBlockSelected);
    }
  }
}