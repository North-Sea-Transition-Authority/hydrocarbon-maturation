package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@WithMockUser
@ContextConfiguration(classes = WellRestController.class)
class WellRestControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SearchSelectorService searchSelectorService;

  @MockBean
  private WellQueryService wellQueryService;

  @SecurityTest
  void searchWonsWells() throws Exception {
    when(searchSelectorService.search(eq("search term"), anyList()))
        .thenReturn(new RestSearchResult(List.of()));

    mockMvc.perform(
            get(SearchSelectorService.route(on(WellRestController.class)
                .searchWonsWells(null)))
                .param("term", "search term")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .with(user(USER)))
        .andExpect(status().isOk());
  }
}