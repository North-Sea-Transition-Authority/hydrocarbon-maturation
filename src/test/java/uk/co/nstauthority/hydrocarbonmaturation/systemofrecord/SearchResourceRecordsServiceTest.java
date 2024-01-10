package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
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
    void buildsResourceRecordSearchResultItem_AssertAllReturnedAndItemsAreBuiltCorrectly() {

      var result1 = buildAndMockResourceRecordQueryResult(
        "resource-name1", "1/12",
        ORGANISATION_ID_1, "Org A",
        List.of(EXTANT_LICENCE_1.licenceId(), EXTANT_LICENCE_2.licenceId()),
        List.of(EXTANT_LICENCE_1.licenceRef(), EXTANT_LICENCE_2.licenceRef()),
        ResponsibleTeamType.EXPLORATION
      );
      var result2 = buildAndMockResourceRecordQueryResult(
          "resource-name2", null,
        ORGANISATION_ID_2, "Org B",
        List.of(NOT_EXTANT_LICENCE_2.licenceId(), NOT_EXTANT_LICENCE_3.licenceId()),
        List.of(NOT_EXTANT_LICENCE_2.licenceRef(), NOT_EXTANT_LICENCE_3.licenceRef()),
        ResponsibleTeamType.EXPLORATION
      );
      when(resourceRecordQueryServiceStrategy.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2));

      var submissionDetailProspect = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
      when(submissionDetailService.getSubmissionDetail(result1.getSubmissionDetailId()))
          .thenReturn(Optional.of(submissionDetailProspect));
      var submissionDetailRelinquishment = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT);
      when(submissionDetailService.getSubmissionDetail(result2.getSubmissionDetailId()))
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
                  result1.getResourceType().getDisplayName(),
                  getActionUrl(result1.getResourceId())
              ),
              tuple(
                  "resource-name2",
                  result2.getResourceType().getDisplayName() + " (Relinquishment)",
                  getActionUrl(result2.getResourceId())
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

    private String getActionUrl(UUID resourceId) {
      return ReverseRouter.route(on(ViewResourceRecordController.class)
          .showResourceRecord(resourceId));
    }

    private ResourceRecordQueryResult buildAndMockResourceRecordQueryResult(String resourceName,
                                                                            String centreOfInterest,
                                                                            int organisationResponsibleId,
                                                                            String orgName,
                                                                            List<Integer> licencesTheResourceIsOn,
                                                                            List<String> licencesDisplayed,
                                                                            ResponsibleTeamType responsibleTeam) {
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, organisationResponsibleId))
          .thenReturn(orgName);
      when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, licencesTheResourceIsOn))
          .thenReturn(licencesDisplayed);
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
          "PROSPECT",
          resourceName,
          centreOfInterest,
          organisationResponsibleId,
          licencesTheResourceIsOn,
          responsibleTeam);
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
  class WhenGettingPreSelectedResourceName {
    private SearchResourceRecordsForm form;

    @BeforeEach
    void setup() {
      form = new SearchResourceRecordsForm();
    }

    @Test
    void noSelectedResourceName_ReturnsEmptyMap() {
      var result = underTest.getPreSelectedResourceName(form);

      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void noSelectedResourceName_VerifyCalls() {
      var generalDetailId = UUID.randomUUID();
      form.setResourceDetailIdForResourceSubmission(String.valueOf(generalDetailId));

      var resourceName = "Resource Name";
      when(submissionResourceService.getResourceDisplayName(generalDetailId)).thenReturn(resourceName);

      var result = underTest.getPreSelectedResourceName(form);

      assertThat(result).isEqualTo(Map.of());

      verify(searchSelectorService).buildPrePopulatedSelection(
          String.valueOf(generalDetailId),
          resourceName
      );
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