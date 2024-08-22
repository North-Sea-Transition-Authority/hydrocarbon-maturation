package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = DeterminedFieldRestController.class)
class DeterminedFieldRestControllerTest extends AbstractSubmissionControllerTest {
  @MockBean
  private SearchSelectorService searchSelectorService;
  @MockBean
  private NewFieldService newFieldService;
  private final String TERM = "term";

  @SecurityTest
  void searchDeterminedFields_whenNoPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(DeterminedFieldRestController.class).searchDeterminedFields(TERM)))
                .with(user(USER))
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isForbidden());
  }
  @SecurityTest
  void searchDeterminedFields_assertOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(DeterminedFieldRestController.class).searchDeterminedFields(TERM)))
                .with(user(USER))
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }
}