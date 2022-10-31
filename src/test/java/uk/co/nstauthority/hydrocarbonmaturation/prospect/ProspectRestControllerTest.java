package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@WithMockUser
@ContextConfiguration(classes = ProspectRestController.class)
class ProspectRestControllerTest extends AbstractControllerTest {

  @MockBean
  private SearchSelectorService searchSelectorService;

  @MockBean
  private UpdateProspectService updateProspectService;

  @BeforeEach
  void setUp() {
    RestSearchResult searchItems = new RestSearchResult(List.of(new RestSearchItem("searchTerm", "searchTerm")));
    when(searchSelectorService.search(eq("searchTerm"), anyList())).thenReturn(searchItems);
  }

  @Test
  void searchProspects() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ProspectRestController.class).searchProspects(null)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }
}