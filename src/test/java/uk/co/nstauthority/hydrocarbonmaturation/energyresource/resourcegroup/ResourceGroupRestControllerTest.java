package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.UUID;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ContextConfiguration(classes = ResourceGroupRestController.class)
class ResourceGroupRestControllerTest extends AbstractControllerTest {
  private static final User USER = UserTestUtil.Builder().build();

  private static final String SEARCH_TERM = "searchTerm";

  @MockitoBean
  private SearchSelectorService searchSelectorService;

  @MockitoBean
  private ResourceGroupQueryService resourceGroupQueryService;


  @SecurityTest
  void searchProspects() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchProspects(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchDiscoveries() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchDiscoveries(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchProspects_whenNotRegulator_assertForbidden() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchProspects(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void searchDiscoveries_whenNotRegulator_assertForbidden() throws Exception {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchDiscoveries(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}