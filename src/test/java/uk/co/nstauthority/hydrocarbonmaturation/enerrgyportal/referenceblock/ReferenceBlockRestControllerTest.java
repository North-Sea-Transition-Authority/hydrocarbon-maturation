package uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@WithMockUser
@ContextConfiguration(classes = ReferenceBlockRestController.class)
class ReferenceBlockRestControllerTest extends AbstractControllerTest {
  private static final String SEARCH_TERM = "search term";

  @MockBean
  private SearchSelectorService searchSelectorService;
  @MockBean
  private ReferenceBlockSearchService referenceBlockSearchService;


  @Test
  void searchQuadrantBlocks() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ReferenceBlockRestController.class)
            .searchQuadrantBlocks(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }
}