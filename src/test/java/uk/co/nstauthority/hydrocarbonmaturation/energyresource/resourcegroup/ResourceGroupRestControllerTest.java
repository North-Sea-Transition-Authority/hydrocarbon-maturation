package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.UUID;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ContextConfiguration(classes = ResourceGroupRestController.class)
class ResourceGroupRestControllerTest extends AbstractControllerTest {
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  private static final String SEARCH_TERM = "searchTerm";

  @MockBean
  private SearchSelectorService searchSelectorService;

  @MockBean
  private ResourceGroupQueryService resourceGroupQueryService;


  @SecurityTest
  void searchProspects() throws Exception {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchProspects(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchDiscoveries() throws Exception {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchDiscoveries(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchProspects_whenNotRegulator_assertForbidden() throws Exception {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchProspects(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void searchDiscoveries_whenNotRegulator_assertForbidden() throws Exception {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceGroupRestController.class)
            .searchDiscoveries(null, UUID.randomUUID())))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}