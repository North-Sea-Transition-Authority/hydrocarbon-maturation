package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.List;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = OrganisationGroupRestController.class)
class OrganisationGroupRestControllerTest extends AbstractControllerTest {

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @SecurityTest
  void getOrganisationGroupSearchResults_thenOk() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var groupList = List.of(
        new OrganisationGroup(1, "Royal Dutch Shell", null, null, null, Collections.emptyList()),
        new OrganisationGroup(2, "Shell", null, null, null, Collections.emptyList())
    );

    when(organisationGroupQueryService.getOrganisationGroupsByName(eq("shell"), anyString()))
        .thenReturn(groupList);
    when(organisationGroupQueryService.organisationGroupsToSearchResult(groupList))
        .thenReturn(new RestSearchResult(List.of(
            new RestSearchItem("1", "Royal Dutch Shell"),
            new RestSearchItem("2", "Shell")
        )));

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
