package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ContextConfiguration(classes = LicenceRestController.class)
class LicenceRestControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  private static final String SEARCH_TERM = "searchTerm";

  @MockitoBean
  private LicenceQueryService licenceQueryService;

  @MockitoBean
  private SearchSelectorService searchSelectorService;

  @SecurityTest
  void searchLicences() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(LicenceRestController.class)
            .searchExtantLicences(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchLicencesByStatus() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(LicenceRestController.class)
            .searchLiveLicences(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

}