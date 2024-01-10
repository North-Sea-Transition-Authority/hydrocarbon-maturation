package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@WithMockUser
@ContextConfiguration(classes = ReferenceBlockRestController.class)
class ReferenceBlockRestControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();
  private static final String SEARCH_TERM = "search term";

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private SearchSelectorService searchSelectorService;
  @MockBean
  private ReferenceBlockQueryService referenceBlockQueryService;

  @SecurityTest
  void searchQuadrantBlocks() throws Exception {
    var referenceBlockView = new ReferenceBlockView("quadAndBlockRef", "quadrant", 1, OnshoreOffshore.OFFSHORE);
    var referenceBlockViews = List.of(referenceBlockView);
    when(referenceBlockQueryService.getReferenceBlocksContaining(SEARCH_TERM)).thenReturn(referenceBlockViews);
    var restSearchItems = referenceBlockViews.stream()
        .map(item -> new RestSearchItem(item.getSelectionId(), item.getSelectionText()))
            .toList();
    when(searchSelectorService.search(SEARCH_TERM, referenceBlockViews)).thenReturn(new RestSearchResult(restSearchItems));

    var returnedReferenceBlockViewsString = mockMvc.perform(get(SearchSelectorService.route(on(ReferenceBlockRestController.class)
            .searchQuadrantBlocks(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var returnedRestSearchItems = objectMapper.readValue(returnedReferenceBlockViewsString, RestSearchResult.class).getResults();
    assertThat(returnedRestSearchItems).containsExactly(restSearchItems.get(0));
  }

  @SecurityTest
  void searchOnshoreQuadrantBlocks() throws Exception {
    var referenceBlockView = new ReferenceBlockView("quadAndBlockRef", "quadrant", 1, OnshoreOffshore.ONSHORE);
    var referenceBlockViews = List.of(referenceBlockView);
    when(referenceBlockQueryService.getReferenceBlocksContaining(SEARCH_TERM, OnshoreOffshore.ONSHORE)).thenReturn(referenceBlockViews);
    var restSearchItems = referenceBlockViews.stream()
        .map(item -> new RestSearchItem(item.getSelectionId(), item.getSelectionText()))
        .toList();
    when(searchSelectorService.search(SEARCH_TERM, referenceBlockViews)).thenReturn(new RestSearchResult(restSearchItems));

    var returnedReferenceBlockViewsString = mockMvc.perform(get(SearchSelectorService.route(on(ReferenceBlockRestController.class)
            .searchOnshoreQuadrantBlocks(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var returnedRestSearchItems = objectMapper.readValue(returnedReferenceBlockViewsString, RestSearchResult.class).getResults();
    assertThat(returnedRestSearchItems).containsExactly(restSearchItems.get(0));
  }

  @SecurityTest
  void searchOffshoreQuadrantBlocks() throws Exception {
    var referenceBlockView = new ReferenceBlockView("quadAndBlockRef", "quadrant", 1, OnshoreOffshore.OFFSHORE);
    var referenceBlockViews = List.of(referenceBlockView);
    when(referenceBlockQueryService.getReferenceBlocksContaining(SEARCH_TERM, OnshoreOffshore.OFFSHORE)).thenReturn(
        referenceBlockViews);
    var restSearchItems = referenceBlockViews.stream()
        .map(item -> new RestSearchItem(item.getSelectionId(), item.getSelectionText()))
        .toList();
    when(searchSelectorService.search(SEARCH_TERM, referenceBlockViews)).thenReturn(
        new RestSearchResult(restSearchItems));

    var returnedReferenceBlockViewsString = mockMvc.perform(
            get(SearchSelectorService.route(on(ReferenceBlockRestController.class)
                .searchOffshoreQuadrantBlocks(null)))
                .param("term", SEARCH_TERM)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    var returnedRestSearchItems = objectMapper.readValue(returnedReferenceBlockViewsString,
        RestSearchResult.class).getResults();
    assertThat(returnedRestSearchItems).containsExactly(restSearchItems.get(0));
  }
}