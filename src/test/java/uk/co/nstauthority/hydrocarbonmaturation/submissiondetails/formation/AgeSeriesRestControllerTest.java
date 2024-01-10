package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ContextConfiguration(classes = AgeSeriesRestController.class)
public class AgeSeriesRestControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  private static final String SEARCH_TERM = "searchTerm";

  @MockBean
  private AgeSeriesSearchService ageSeriesSearchService;

  @MockBean
  private SearchSelectorService searchSelectorService;

  @SecurityTest
  void getAgeSeriesList() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(AgeSeriesRestController.class)
            .getAgeSeriesList(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }
}
