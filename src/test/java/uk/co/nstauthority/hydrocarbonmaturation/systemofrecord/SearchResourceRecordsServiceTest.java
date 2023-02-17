package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.jetbrains.annotations.NotNull;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@ExtendWith(MockitoExtension.class)
class SearchResourceRecordsServiceTest {
  @Mock
  private ResourceRecordQueryService resourceRecordQueryService;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @Mock
  private SearchSelectorService searchSelectorService;
  @Mock
  private SubmissionResourceService submissionResourceService;
  @Mock
  private LicenceQueryService licenceQueryService;
  @InjectMocks
  private SearchResourceRecordsService underTest;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @Test
  void getSearchResourceRecordsForm_assertResourceTypesAndOpportunityTypes() {
    var returnedForm = underTest.getSearchResourceRecordsForm();
    assertThat(returnedForm)
        .extracting(
            SearchResourceRecordsForm::getResourceTypes,
            SearchResourceRecordsForm::getOpportunityTypes)
        .containsExactly(
            List.of(
                "PROSPECT",
                "DISCOVERY"),
            List.of(
                "LEAD",
                "PROSPECT",
                "DRILL_READY_PROSPECT",
                "DRY_WELL",
                "DISCOVERY",
                "OTHER",
                "NOT_SET"));
  }

  @Test
  void getOpportunityTypes() {
    AssertionsForInterfaceTypes.assertThat(underTest.getOpportunityTypes())
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
    AssertionsForInterfaceTypes.assertThat(underTest.getResourceTypes())
        .containsExactly(
            entry(ResourceType.PROSPECT.name(), "Prospect"),
            entry(ResourceType.DISCOVERY.name(), "Discovery")
        );
  }

  @Nested
  class WhenGettingResourceRecordSearchResults {

    @Mock
    private SearchResourceRecordsForm form;

    @Test
    void buildsResourceRecordSearchResultItem_AssertItemsAreBuiltCorrectly() {
      var licencesDisplayed1 = List.of("L1", "L2");
      var result1 =
          buildAndMockResourceRecordQueryResult("resource-name1", "1/12", 1, "Org A", List.of(1, 2), licencesDisplayed1);
      var licencesDisplayed2 = List.of("L3", "L4");
      var result2 =
          buildAndMockResourceRecordQueryResult("resource-name2", null, 2, "Org B", List.of(3, 4), licencesDisplayed2);
      when(resourceRecordQueryService.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2));

      var results = underTest.getResourceRecordSearchResults(form);

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
                  getActionUrl()
              ),
              tuple(
                  "resource-name2",
                  result2.getResourceType().getDisplayName(),
                  getActionUrl()
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("organisationResponsible", "Org A"),
              entry("licences", licencesDisplayed1)
          );

      assertThat(results.get(1).modelProperties().getProperties())
          .containsExactly(
              entry("organisationResponsible", "Org B"),
              entry("licences", licencesDisplayed2)
          );
    }

    @NotNull
    private String getActionUrl() {
      return ReverseRouter.route(on(SearchResourceRecordsController.class)
          .showSearchResourceRecordsScreen());
    }

    @NotNull
    private ResourceRecordQueryResult buildAndMockResourceRecordQueryResult(String resourceName,
                                                                            String centreOfInterest,
                                                                            int organisationResponsibleId,
                                                                            String orgName,
                                                                            List<Integer> licencesTheResourceIsOn,
                                                                            List<String> licencesDisplayed) {
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(organisationResponsibleId)).thenReturn(orgName);
      when(submissionDetailDisplayService.getLicencesForDisplay(licencesTheResourceIsOn)).thenReturn(licencesDisplayed);

      return new ResourceRecordQueryResult(
          UUID.randomUUID(),
          "PROSPECT",
          resourceName,
          centreOfInterest,
          organisationResponsibleId,
          licencesTheResourceIsOn,
          Timestamp.from(Instant.now().minusSeconds(1)),
          Timestamp.from(Instant.now())
      );
    }

  }

  @Test
  void maxResultsReturned_isAtLimit() {
    Supplier<ResourceRecordSearchResultItem> result = ResourceRecordSearchResultItem::empty;
    var results = Stream.generate(result).limit(ResourceRecordQueryService.MAX_RESULTS).toList();
    assertThat(underTest.maxResultsReturned(results)).isTrue();
  }

  @Test
  void maxResultsReturned_isNotAtLimit() {
    Supplier<ResourceRecordSearchResultItem> result = ResourceRecordSearchResultItem::empty;
    var results = Stream.generate(result).limit(ResourceRecordQueryService.MAX_RESULTS - 1).toList();
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
      when(licenceQueryService.getLicenceById(id))
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