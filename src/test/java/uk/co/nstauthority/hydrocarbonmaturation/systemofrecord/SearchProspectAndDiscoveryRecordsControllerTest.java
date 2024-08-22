package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@ContextConfiguration(classes = SearchProspectAndDiscoveryRecordsController.class)
class SearchProspectAndDiscoveryRecordsControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  private SearchResourceRecordsService searchResourceRecordsService;
  @MockBean
  private ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  @SecurityTest
  void showSearchResourceRecordsScreen_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void searchResourceRecords_whenRegulator_assertStatusOk() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
            .showSearchResourceRecordsScreen(null, null))));
  }

  @Test
  void searchResourceRecords_whenOperator_assertStatusOk() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
            .showSearchResourceRecordsScreen(null, null))));
  }

  @Test
  void showSearchResourceRecordsScreen_whenOperator_assertStatusOk() throws Exception {
    var form = new SearchResourceRecordsForm();
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
                .showSearchResourceRecordsScreen(null, null)))
                .with(user(USER)))
        .andExpect(status().isOk());
    assertThat(form.isShowOnlyPreferredViews()).isFalse();
  }

  @Test
  void showSearchResourceRecordsScreen_whenRegulator_whenFilterInSessionAndShowResults_verifyMethodCalls() throws Exception {
    var operatorQueryService = mock(OperatorResourceRecordQueryService.class);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(resourceRecordQueryServiceFactory.getQueryServiceStrategy(USER))
        .thenReturn(operatorQueryService);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
                .showSearchResourceRecordsScreen(null, null)))
                .with(user(USER))
                .flashAttr("searchProspectAndDiscoveryRecordsFilter", getFullFilter(true)))
        .andExpect(status().isOk());

    verify(searchResourceRecordsService, times(1))
        .getResourceRecordSearchResults(any(SearchResourceRecordsForm.class), eq(operatorQueryService), eq(USER));
  }

  @Test
  void clearSearchResourceRecordFilter_verifyFilterDefaultsSet() throws Exception {
    var filter = getFullFilter(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
                .clearSearchResourceRecordFilter(null, null)))
                .with(user(USER))
                .flashAttr("searchProspectAndDiscoveryRecordsFilter", filter))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
            .showSearchResourceRecordsScreen(null, null))));

    assertThat(filter)
        .hasAllNullFieldsOrPropertiesExcept(
            "opportunityTypes",
            "resourceTypes",
            "showResults",
            "showOnlyPreferredViews",
            "geographicalArea")
        .extracting(
            SearchProspectAndDiscoveryRecordsFilter::getOpportunityTypes,
            SearchProspectAndDiscoveryRecordsFilter::getResourceTypes,
            SearchProspectAndDiscoveryRecordsFilter::isShowResults,
            SearchProspectAndDiscoveryRecordsFilter::isShowOnlyPreferredViews,
            SearchProspectAndDiscoveryRecordsFilter::getGeographicalArea)
        .containsExactly(
            List.of("LEAD",
                "PROSPECT",
                "DRILL_READY_PROSPECT",
                "DRY_WELL",
                "DISCOVERY",
                "OTHER",
                "NOT_SET"),
            List.of("PROSPECT", "DISCOVERY"),
            false,
            true,
            List.of());
  }

  @Test
  void showSearchResourceRecordsScreenModelAndView_whenRegulator_assertModelProperties() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(SearchResourceRecordsForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("pageTitle", SearchProspectAndDiscoveryRecordsController.PAGE_TITLE),
            entry("backLinkUrl", ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage())),
            entry("filtersAdded", false),
            entry("isRegulatorOrAreaTeamUser", true),
            entry("clearFilterUrl", ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
                .clearSearchResourceRecordFilter(null, null))),
            entry("resourceRecordSearchResultItems", Collections.emptyList()),
            entry("resourceApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspectsAndDiscoveries(null, null))),
            entry("resourceName", Map.of()),
            entry("organisationUnitEndpoint", SearchSelectorService.route(on(OrganisationUnitRestController.class)
                .searchOrganisationUnits(null, null))),
            entry("responsibleOrganisation", Map.of()),
            entry("quadrantBlocksApiEndpoint", SearchSelectorService.route(on(ReferenceBlockRestController.class)
                .searchQuadrantBlocks(null))),
            entry("centreOfInterestBlock", Map.of()),
            entry("maxResultsReturned", false),
            entry("geographicalArea", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class))
        );
  }

  SearchProspectAndDiscoveryRecordsFilter getFullFilter(boolean showResults) {
    var filter = new SearchProspectAndDiscoveryRecordsFilter();
    filter.setShowResults(showResults);
    filter.setCentreOfInterestBlock("2/15");
    filter.setGeographicalArea(List.of(GeographicalArea.ONSHORE.name()));
    filter.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    filter.setShowOnlyPreferredViews(false);
    filter.setOpportunityTypes(List.of(OpportunityType.DISCOVERY.name()));
    filter.setLicenceId("2");
    filter.setLicenceRound("2");
    filter.setResourceNameFromResourceDetail("random id");
    filter.setResponsibleOrganisationId(1);
    return filter;
  }
}