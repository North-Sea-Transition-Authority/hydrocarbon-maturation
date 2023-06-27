package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Set;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = DeterminedFieldRestController.class)
class DeterminedFieldRestControllerTest extends AbstractSubmissionControllerTest {
  @MockBean
  private SearchSelectorService searchSelectorService;
  @MockBean
  private NewFieldService newFieldService;
  private final String TERM = "term";
  private final Set<RolePermission> REQUIRED_PERMISSIONS = Set.of(
      RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
      RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
      RolePermission.ONSHORE_AREA_TEAM_MEMBER
  );

  @SecurityTest
  void searchDeterminedFields_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, REQUIRED_PERMISSIONS))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(DeterminedFieldRestController.class).searchDeterminedFields(TERM)))
                .with(user(USER))
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isForbidden());
  }
  @SecurityTest
  void searchDeterminedFields_assertOk() throws Exception {
    when(permissionService.hasPermission(USER, REQUIRED_PERMISSIONS))
        .thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(DeterminedFieldRestController.class).searchDeterminedFields(TERM)))
                .with(user(USER))
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }
}