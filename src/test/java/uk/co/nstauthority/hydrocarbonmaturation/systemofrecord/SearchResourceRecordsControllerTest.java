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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = SearchResourceRecordsController.class)
class SearchResourceRecordsControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SearchResourceRecordsService searchResourceRecordsService;
  @MockBean
  private ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  @SecurityTest
  void showSearchResourceRecordsScreen_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen())))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void searchResourceRecords_whenRegulator_assertStatusOk() throws Exception {
    var form = new SearchResourceRecordsForm();
    var regulatorQueryService = mock(RegulatorResourceRecordQueryService.class);
    when(resourceRecordQueryServiceFactory.getQueryServiceStrategy())
        .thenReturn(regulatorQueryService);
    mockMvc.perform(
            post(ReverseRouter.route(on(SearchResourceRecordsController.class).searchResourceRecords(form)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(searchResourceRecordsService, times(1))
        .getResourceRecordSearchResults(
            any(SearchResourceRecordsForm.class),
            eq(regulatorQueryService));
  }

  @Test
  void searchResourceRecords_whenOperator_assertStatusOk() throws Exception {
    var form = new SearchResourceRecordsForm();
    var operatorQueryService = mock(OperatorResourceRecordQueryService.class);
    when(resourceRecordQueryServiceFactory.getQueryServiceStrategy())
        .thenReturn(operatorQueryService);
    mockMvc.perform(
            post(ReverseRouter.route(on(SearchResourceRecordsController.class).searchResourceRecords(form)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(searchResourceRecordsService, times(1))
        .getResourceRecordSearchResults(
            any(SearchResourceRecordsForm.class),
            eq(operatorQueryService));
  }

  @Test
  void showSearchResourceRecordsScreenModelAndView_assertModelProperties() throws Exception {
    var form = new SearchResourceRecordsForm();
    when(searchResourceRecordsService.getSearchResourceRecordsForm())
        .thenReturn(form);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen()))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(SearchResourceRecordsForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("pageTitle", SearchResourceRecordsController.PAGE_TITLE),
            entry("filtersAdded", false),
            entry("form", form),
            entry("clearFilterUrl", ReverseRouter.route(on(SearchResourceRecordsController.class)
                .showSearchResourceRecordsScreen())),
            entry("resourceRecordSearchResultItems", Collections.emptyList()),
            entry("resourceApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
                .searchResources(null))),
            entry("resourceName", Map.of()),
            entry("organisationUnitEndpoint", SearchSelectorService.route(on(OrganisationUnitRestController.class)
                .searchOrganisationUnits(null))),
            entry("responsibleOrganisation", Map.of()),
            entry("quadrantBlocksApiEndpoint", SearchSelectorService.route(on(ReferenceBlockRestController.class)
                .searchQuadrantBlocks(null))),
            entry("centreOfInterestBlock", Map.of()),
            entry("maxResultsReturned", false)
        );
  }

}