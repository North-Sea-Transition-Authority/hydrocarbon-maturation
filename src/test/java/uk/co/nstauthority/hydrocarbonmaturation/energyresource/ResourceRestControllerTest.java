package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchResourceRecordsQueryService;

@ContextConfiguration(classes = ResourceRestController.class)
class ResourceRestControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  private SearchSelectorService searchSelectorService;

  @MockBean
  private SubmissionQueryService submissionQueryService;

  @MockBean
  private FieldQueryService fieldQueryService;

  @MockBean
  private ReservoirQueryService reservoirQueryService;

  @MockBean
  private SearchResourceRecordsQueryService searchResourceRecordsQueryService;

  @BeforeEach
  void setUp() {
    var searchItems = new RestSearchResult(List.of(new RestSearchItem("searchTerm", "searchTerm")));
    when(searchSelectorService.search(eq("searchTerm"), anyList())).thenReturn(searchItems);
  }

  @SecurityTest
  void searchProspects() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class)
            .searchProspects(null, SubmissionType.PROSPECT_UPDATE)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchDiscoveries() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class)
            .searchDiscoveries(null, SubmissionType.UPDATE_DISCOVERY)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchDiscoveriesNotInGroupOrPreferredView() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class)
            .searchDiscoveriesNotInGroupOrPreferredView(null)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchResources() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class)
            .searchResources(null, null)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchFieldsByFieldName() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class)
            .searchFieldsByFieldName( null)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchFields() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class)
            .searchFields(null, SubmissionType.CREATE_CSR, null)))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void searchDiscoveryReservoirs() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(ResourceRestController.class)
            .searchDiscoveryReservoirs(null, "submissionDetailId")))
            .param("term", "searchTerm")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }
}