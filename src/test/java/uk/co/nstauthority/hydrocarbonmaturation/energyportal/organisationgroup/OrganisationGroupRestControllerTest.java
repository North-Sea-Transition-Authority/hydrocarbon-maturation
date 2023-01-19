package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = OrganisationGroupRestController.class)
class OrganisationGroupRestControllerTest extends AbstractControllerTest {

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @MockBean
  private SearchSelectorService searchSelectorService;

  @SecurityTest
  void getOrganisationGroupSearchResults_thenOk() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var groupList = List.of(
        OrganisationGroupTestUtil.createOrganisationGroupDto(1, "Royal Dutch Shell"),
        OrganisationGroupTestUtil.createOrganisationGroupDto(2, "Shell")
    );

    when(organisationGroupQueryService.getOrganisationGroupsByName("shell"))
        .thenReturn(groupList);

    mockMvc.perform(
        get(
            ReverseRouter.route(on(OrganisationGroupRestController.class).getOrganisationGroupSearchResults(null)))
            .param("term", "shell")
            .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getOrganisationGroupSearchResults_thenUnauthorised() throws Exception {
    mockMvc.perform(
        get(
            ReverseRouter.route(on(OrganisationGroupRestController.class).getOrganisationGroupSearchResults(null)))
            .param("term", "shell"))
        .andExpect(status().isUnauthorized());
  }
}
