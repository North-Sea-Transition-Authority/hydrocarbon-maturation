package uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@WithMockUser
@ContextConfiguration(classes = ReferenceBlockRestController.class)
class ReferenceBlockRestControllerTest extends AbstractControllerTest {
  private static final String SEARCH_TERM = "search term";

  @MockBean
  private SearchSelectorService searchSelectorService;
  @MockBean
  private ReferenceBlockSearchService referenceBlockSearchService;


  @Test
  void searchQuadrantBlocks_VerifyCalls() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ReferenceBlockRestController.class).searchQuadrantBlocks(SEARCH_TERM))))
        .andExpect(status().isOk());

    verify(referenceBlockSearchService, times(1)).getReferenceBlocksBy(SEARCH_TERM);
  }
}