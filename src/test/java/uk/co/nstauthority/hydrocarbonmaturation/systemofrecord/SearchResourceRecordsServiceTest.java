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

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@ExtendWith(MockitoExtension.class)
class SearchResourceRecordsServiceTest {

  @Mock
  private ResourceRecordQueryServiceStrategy resourceRecordQueryServiceStrategy;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @Mock
  private SearchSelectorService searchSelectorService;
  @Mock
  private SubmissionResourceService submissionResourceService;
  @Mock
  private LicenceQueryService licenceQueryService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @InjectMocks
  private SearchResourceRecordsService underTest;

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
    private final LicenceView EXTANT_LICENCE_1 = buildLicenceView(1, "L1", LicenceStatus.EXTANT);
    private final LicenceView EXTANT_LICENCE_2 = buildLicenceView(2, "L2", LicenceStatus.EXTANT);
    private final LicenceView NOT_EXTANT_LICENCE_2 = buildLicenceView(3, "F1", LicenceStatus.EXPIRED);
    private final LicenceView NOT_EXTANT_LICENCE_3 = buildLicenceView(4, "F2", LicenceStatus.EXPIRED);
    private final Map<Integer, LicenceView> LICENCE_MAP = Map.of(
        1, EXTANT_LICENCE_1,
        2, EXTANT_LICENCE_2,
        3, NOT_EXTANT_LICENCE_2,
        4, NOT_EXTANT_LICENCE_3
    );
    private final int ORGANISATION_ID_1 = 1;
    private final int ORGANISATION_ID_2 = 2;
    private final Map<Integer, OrganisationUnitDto> ORGANISATION_UNIT_MAP = Map.of(
        ORGANISATION_ID_1, new OrganisationUnitDto(ORGANISATION_ID_1, "Org A", null),
        ORGANISATION_ID_2, new OrganisationUnitDto(ORGANISATION_ID_2, "Org B", null)
    );
    @Mock
    private SearchResourceRecordsForm form;

    @BeforeEach
    void setUp() {
      when(organisationUnitQueryService.getOrganisationUnitMapByIds(anyList())).thenReturn(ORGANISATION_UNIT_MAP);
      when(licenceQueryService.getLicenceMapByIds(anyList())).thenReturn(LICENCE_MAP);
    }

    @Test
    void buildsDiscoveryAndProspectRecordSearchResultItem_AssertAllReturnedAndItemsAreBuiltCorrectly() {

      var result1 = buildAndMockResourceRecordQueryResult(
        "resource-name1", ResourceType.PROSPECT, "1/12",
        ORGANISATION_ID_1, "Org A",
        List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
        List.of(EXTANT_LICENCE_1.licenceRef(), EXTANT_LICENCE_2.licenceRef()),
        ResponsibleTeamType.EXPLORATION, null, null
      );
      var result2 = buildAndMockResourceRecordQueryResult(
          "resource-name2", ResourceType.DISCOVERY, null,
        ORGANISATION_ID_2, "Org B",
        List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId()),
        List.of(NOT_EXTANT_LICENCE_2.licenceRef(), NOT_EXTANT_LICENCE_3.licenceRef()),
        ResponsibleTeamType.EXPLORATION, null, null
      );
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2));

      var submissionDetailProspect = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
      when(submissionDetailService.getSubmissionDetail(result1.submissionDetailId()))
          .thenReturn(Optional.of(submissionDetailProspect));
      var submissionDetailRelinquishment = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT);
      when(submissionDetailService.getSubmissionDetail(result2.submissionDetailId()))
          .thenReturn(Optional.of(submissionDetailRelinquishment));

      var results = underTest.getResourceRecordSearchResults(form, resourceRecordQueryServiceStrategy);

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
                  result2.resourceType().getDisplayName() + " (Relinquishment)",
                  getActionUrl(result2.resourceId())
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.EXPLORATION.getDisplayName()),
              entry("organisationResponsible", "Org A"),
              entry("licences", List.of("L1", "L2"))
          );

      assertThat(results.get(1).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.EXPLORATION.getDisplayName()),
              entry("organisationResponsible", "Org B"),
              entry("licences", List.of("F1", "F2"))
          );
    }

    @Test
    void buildsFieldRecordSearchResultItem_AssertAllReturnedAndItemsAreBuiltCorrectly() {

      var result1 = buildAndMockResourceRecordQueryResult(
          "field-name1", ResourceType.FIELD, "1/12",
          ORGANISATION_ID_1, "Org A",
          List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
          List.of(EXTANT_LICENCE_1.licenceRef(), EXTANT_LICENCE_2.licenceRef()),
          ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, null, FieldStatus.STATUS_200
      );
      var result2 = buildAndMockResourceRecordQueryResult(
          "field-name2", ResourceType.FIELD, null,
          ORGANISATION_ID_2, "Org B",
          List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId()),
          List.of(NOT_EXTANT_LICENCE_2.licenceRef(), NOT_EXTANT_LICENCE_3.licenceRef()),
          ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA, "Parent field name", FieldStatus.STATUS_400
      );
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2));

      var submissionDetailCsr = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      when(submissionDetailService.getSubmissionDetail(result1.submissionDetailId()))
          .thenReturn(Optional.of(submissionDetailCsr));
      var submissionDetailFdp = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
      when(submissionDetailService.getSubmissionDetail(result2.submissionDetailId()))
          .thenReturn(Optional.of(submissionDetailFdp));

      var results = underTest.getResourceRecordSearchResults(form, resourceRecordQueryServiceStrategy);

      assertThat(results)
          .extracting(
              ResourceRecordSearchResultItem::headingText,
              ResourceRecordSearchResultItem::captionText,
              ResourceRecordSearchResultItem::actionUrl
          )
          .containsExactly(
              tuple(
                  "field-name1 (1/12)",
                  "Field",
                  getActionUrl(result1.resourceId())
              ),
              tuple(
                  "field-name2",
                  "Field",
                  getActionUrl(result2.resourceId())
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND.getDisplayName()),
              entry("organisationResponsible", "Org A"),
              entry("fieldStatus", result1.fieldStatus().getDisplayName()),
              entry("parentField", "No parent field")
          );

      assertThat(results.get(1).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getDisplayName()),
              entry("organisationResponsible", "Org B"),
              entry("fieldStatus", result2.fieldStatus().getDisplayName()),
              entry("parentField", "Parent field name")
          );
    }

    @Test
    void buildsFieldRecordSearchResultItem_whenReturnedFieldRecordHasNoSubmission_AssertAllReturnedAndItemsAreBuiltCorrectly() {

      var result = buildAndMockResourceRecordQueryResult(
          "field-name1", ResourceType.FIELD, null,
          ORGANISATION_ID_1, "Org A",
          List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
          List.of(EXTANT_LICENCE_1.licenceRef(), EXTANT_LICENCE_2.licenceRef()),
          ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, null, FieldStatus.STATUS_200
      );
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result));

      var results = underTest.getResourceRecordSearchResults(form, resourceRecordQueryServiceStrategy);

      assertThat(results)
          .extracting(
              ResourceRecordSearchResultItem::headingText,
              ResourceRecordSearchResultItem::captionText,
              ResourceRecordSearchResultItem::actionUrl
          )
          .containsExactly(
              tuple(
                  "field-name1",
                  "Field",
                  getActionUrl(result.resourceId())
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("teamResponsible", ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND.getDisplayName()),
              entry("organisationResponsible", "Org A"),
              entry("fieldStatus", result.fieldStatus().getDisplayName()),
              entry("parentField", "No parent field")
          );
    }

    private String getActionUrl(UUID resourceId) {
      return ReverseRouter.route(on(ViewResourceRecordController.class)
          .showResourceRecord(resourceId));
    }

    private ResourceRecordQueryResult buildAndMockResourceRecordQueryResult(String resourceName,
                                                                            ResourceType resourceType, String centreOfInterest,
                                                                            int organisationResponsibleId,
                                                                            String orgName,
                                                                            List<Integer> licencesTheResourceIsOn,
                                                                            List<String> licencesDisplayed,
                                                                            ResponsibleTeamType responsibleTeam,
                                                                            String parentFieldName,
                                                                            FieldStatus fieldStatus) {
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, organisationResponsibleId)).thenReturn(orgName);
      if (!ResourceType.FIELD.equals(resourceType)) {
        when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, licencesTheResourceIsOn)).thenReturn(licencesDisplayed);
      }
      when(submissionDetailDisplayService.getTeamResponsibleDisplay(responsibleTeam)).thenReturn(responsibleTeam.getDisplayName());

      if (centreOfInterest != null) {
        when(submissionDetailDisplayService
            .getResourceNameAndCentreOfInterestBlockDisplay(resourceName, centreOfInterest))
            .thenReturn(resourceName + " (" + centreOfInterest + ")");
      } else {
        when(submissionDetailDisplayService
            .getResourceNameAndCentreOfInterestBlockDisplay(resourceName, centreOfInterest))
            .thenReturn(resourceName);
      }
      return new ResourceRecordQueryResult(
          UUID.randomUUID(),
          UUID.randomUUID(),
          resourceType,
          resourceName,
          centreOfInterest,
          organisationResponsibleId,
          licencesTheResourceIsOn,
          responsibleTeam,
          parentFieldName == null ? "No parent field" : parentFieldName,
          fieldStatus);
    }

  }

  private static LicenceView buildLicenceView(int id, String ref, LicenceStatus status) {
    return LicenceViewTestUtil.Builder()
        .withLicenceId(id)
        .withLicenceRef(ref)
        .withLicenceStatus(status)
        .build();
  }

  @Test
  void maxResultsReturned_isAtLimit() {
    Supplier<ResourceRecordSearchResultItem> result = ResourceRecordSearchResultItem::empty;
    var results = Stream.generate(result).limit(ResourceQueryCommonService.MAX_RESULTS).toList();
    assertThat(underTest.maxResultsReturned(results)).isTrue();
  }

  @Test
  void maxResultsReturned_isNotAtLimit() {
    Supplier<ResourceRecordSearchResultItem> result = ResourceRecordSearchResultItem::empty;
    var results = Stream.generate(result).limit(ResourceQueryCommonService.MAX_RESULTS - 1).toList();
    assertThat(underTest.maxResultsReturned(results)).isFalse();
  }

  @Nested
  class WhenGettingResourceRecordSearchResultsDistinctNames {

    @Test
    void getResourceRecordSearchResultsDistinctResourceNames() {
      var result1 = newSearchResultWithResourceName("Result Z");
      var result2 = newSearchResultWithResourceName("Result Z");
      var result3 = newSearchResultWithResourceName("Result A");
      var result4 = newSearchResultWithResourceName("Result b");
      var searchResults = List.of(result1, result2, result3, result4);

      var form = new SearchResourceRecordsForm();

      var queryServiceStrategy = mock(ResourceRecordQueryServiceStrategy.class);
      when(queryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(searchResults);

      var expectedResult = Stream.of(result3, result4, result2)
          .map(ResourceRecordQueryResult::resourceName).toList();

      assertThat(underTest.getResourceRecordSearchResultsDistinctResourceNames(form, queryServiceStrategy))
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    private ResourceRecordQueryResult newSearchResultWithResourceName(String resourceName) {
      return new ResourceRecordQueryResult(null, null, null,
          resourceName, null, null,
          null, null, null, null);
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

      var result = underTest.getPreSelectedOrganisationUnit(form);

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
      var licenceView = new LicenceView("ref", Integer.parseInt(id), LicenceStatus.EXTANT);
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

      var result = underTest.getPreSelectedQuadAndBlock(form);

      verify(searchSelectorService).buildPrePopulatedSelection(quadBlockSelected, quadBlockSelected);
    }
  }
}