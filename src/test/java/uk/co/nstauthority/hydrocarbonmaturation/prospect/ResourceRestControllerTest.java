package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.SubmissionQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ContextConfiguration(classes = ResourceRestController.class)
class ResourceRestControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SearchSelectorService searchSelectorService;

  @MockBean
  private SubmissionQueryService submissionQueryService;

  @BeforeEach
  void setUp() {
    var searchItems = new RestSearchResult(List.of(new RestSearchItem("searchTerm", "searchTerm")));
    when(searchSelectorService.search(eq("searchTerm"), anyList())).thenReturn(searchItems);
  }

  @Test
  void searchProspects() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class).searchProspects(null, SubmissionType.PROSPECT_UPDATE)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void searchDiscoveries() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class).searchDiscoveries(null)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }
}