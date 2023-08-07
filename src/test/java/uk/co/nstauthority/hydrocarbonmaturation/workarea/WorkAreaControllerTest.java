package uk.co.nstauthority.hydrocarbonmaturation.workarea;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = WorkAreaController.class)
class WorkAreaControllerTest extends AbstractControllerTest {

  @MockBean
  private WorkAreaItemService workAreaItemService;
  @MockBean
  private PermissionService permissionService;

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @SecurityTest
  void getWorkArea_assertHttpOk() throws Exception {
    List<WorkAreaItem> workAreaItems = List.of();
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);

    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/workarea/workArea"))
        .andExpect(model().attribute("startSubmissionUrl",
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType())))
        .andExpect(model().attribute("workAreaItems", workAreaItems));
  }

  @Test
  void getWorkArea_whenHasPermission_assertCanCreateSubmissionsTrue() throws Exception {
    List<WorkAreaItem> workAreaItems = List.of();
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);
    when(permissionService.hasPermission(USER, Set.of(
            RolePermission.EXPLORATION,
            RolePermission.INDUSTRY_SUBMIT,
            RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
            RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
            RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/workarea/workArea"))
        .andExpect(model().attribute("canCreateSubmissions", true));
  }

  @Test
  void getWorkArea_whenDoesNotHavePermission_assertCanCreateSubmissionsFalse() throws Exception {
    List<WorkAreaItem> workAreaItems = List.of();
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION,
        RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/workarea/workArea"))
        .andExpect(model().attribute("canCreateSubmissions", false));
  }
}
