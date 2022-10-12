package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@WithMockUser
@ContextConfiguration(classes = ProspectRestController.class)
class ProspectRestControllerTest extends AbstractControllerTest {

  private static final String REST_URL = "/api/prospects";

  @MockBean
  private SearchSelectorService searchSelectorService;

  @MockBean
  private UpdateProspectService updateProspectService;

  @BeforeEach
  void setUp() {
    List<RestSearchItem> searchItems = List.of(new RestSearchItem("searchTerm", "searchTerm"));
    when(searchSelectorService.search(eq("searchTerm"), anyList())).thenReturn(searchItems);
  }

  @Test
  void searchProspects_noProspects() throws Exception {mockMvc.perform(
       get(REST_URL)
           .param("term", "searchTerm")
           .accept(MediaType.APPLICATION_JSON_VALUE))
       .andExpect(status().isOk());
  }
}