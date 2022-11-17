package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ContextConfiguration(classes = OrganisationUnitRestController.class)
class OrganisationUnitRestControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  private static final String SEARCH_TERM = "searchTerm";

  @MockBean
  OrganisationUnitQueryService organisationUnitQueryService;

  @MockBean
  SearchSelectorService searchSelectorService;

  @Test
  void searchLicenceApplications() throws Exception {
    mockMvc.perform(get(SearchSelectorService.route(on(OrganisationUnitRestController.class)
            .searchOrganisationUnits(null)))
            .param("term", SEARCH_TERM)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .with(user(USER)))
        .andExpect(status().isOk());
  }
}
