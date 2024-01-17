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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = SearchFieldRecordsController.class)
class SearchFieldRecordsControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  private SearchResourceRecordsService searchResourceRecordsService;
  @MockBean
  private ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  @SecurityTest
  void showSearchFieldRecordsScreen_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void showSearchFieldRecordsScreen_whenOperator_assertStatusOk() throws Exception {
    var form = new SearchResourceRecordsForm();
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    when(areaTeamService.isAreaUser(USER)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class)
                .showSearchFieldRecordsScreen(null)))
                .with(user(USER)))
        .andExpect(status().isOk());
    assertThat(form.isShowOnlyPreferredViews()).isFalse();
  }

  @Test
  void showSearchFieldRecordsScreen_whenRegulator_assertStatusOk() throws Exception {
    var form = new SearchResourceRecordsForm();
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);
    when(areaTeamService.isAreaUser(USER)).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class)
                .showSearchFieldRecordsScreen(null)))
                .with(user(USER)))
        .andExpect(status().isOk());
    assertThat(form.isShowOnlyPreferredViews()).isFalse();
  }

  @Test
  void showSearchResourceRecordsScreen_whenRegulator_whenFilterInSessionAndShowResults_verifyMethodCalls() throws Exception {
    var operatorQueryService = mock(OperatorResourceRecordQueryService.class);
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);
    when(resourceRecordQueryServiceFactory.getQueryServiceStrategy())
        .thenReturn(operatorQueryService);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class)
                .showSearchFieldRecordsScreen(null)))
                .with(user(USER))
                .flashAttr("searchFieldRecordsFilter", getFullFilter(true)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView()
        .getModelMap());

    verify(searchResourceRecordsService, times(1))
        .getResourceRecordSearchResults(any(SearchResourceRecordsForm.class), eq(operatorQueryService));

    assertEquals(SearchResourceRecordsForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("pageTitle", SearchFieldRecordsController.PAGE_TITLE),
            entry("backLinkUrl", ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage())),
            entry("filtersAdded", true),
            entry("clearFilterUrl", ReverseRouter.route(on(SearchFieldRecordsController.class)
                .clearSearchResourceRecordFilter(null, null))),
            entry("resourceRecordSearchResultItems", Collections.emptyList()),
            entry("maxResultsReturned", false)
        );
  }

  @Test
  void clearSearchResourceRecordFilter_verifyFilterDefaultsSet() throws Exception {
    var filter = getFullFilter(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(SearchFieldRecordsController.class)
                .clearSearchResourceRecordFilter(null, null)))
                .with(user(USER))
                .flashAttr("searchFieldRecordsFilter", filter))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SearchFieldRecordsController.class)
            .showSearchFieldRecordsScreen(null))));
  }

  private SearchFieldRecordsFilter getFullFilter(boolean showResults) {
    var filter = new SearchFieldRecordsFilter();
    filter.setShowResults(showResults);
    return filter;
  }
}