package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ContextConfiguration(classes = LicenceApplicationRestController.class)
class LicenceApplicationRestControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  private static final String SEARCH_TERM = "searchTerm";

  @MockitoBean
  LicenceApplicationQueryService licenceApplicationQueryService;

  @MockitoBean
  SearchSelectorService searchSelectorService;

  @BeforeEach
  void setUp() {
  }

  @SecurityTest
  void searchLicenceApplications() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(LicenceApplicationRestController.class)
            .searchLicenceApplicationsWithManualEntry(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }
}
