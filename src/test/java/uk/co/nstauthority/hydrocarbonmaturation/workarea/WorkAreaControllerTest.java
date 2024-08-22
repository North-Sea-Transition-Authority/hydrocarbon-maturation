package uk.co.nstauthority.hydrocarbonmaturation.workarea;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleTestUtil;

@ContextConfiguration(classes = WorkAreaController.class)
class WorkAreaControllerTest extends AbstractControllerTest {

  @MockBean
  private WorkAreaItemService workAreaItemService;

  private static final User USER = UserTestUtil.Builder().build();

  private Timer timer;

  @BeforeEach
  void setUp() {
    timer = mock(Timer.class);
    when(metricsProvider.getWorkAreaRequestTimer()).thenReturn(timer);
  }

  @SecurityTest
  void getWorkArea_assertHttpOk() throws Exception {
    List<WorkAreaItem> workAreaItems = List.of();
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);

    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/workarea/workArea"))
        .andExpect(model().attribute("startSubmissionUrl",
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType(null))))
        .andExpect(model().attribute("workAreaItems", workAreaItems));
  }

  @ParameterizedTest
  @EnumSource(value = Role.class, names = {"INDUSTRY_SUBMIT", "EXPLORATION", "MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM"})
  void getWorkArea_whenHasPermission_assertCanCreateSubmissionsTrue(Role role) throws Exception {
    List<WorkAreaItem> workAreaItems = List.of();
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);
    var teamRole = TeamRoleTestUtil.Builder().withRole(role).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/workarea/workArea"))
        .andExpect(model().attribute("canCreateSubmissions", true));
  }

  @ParameterizedTest
  @EnumSource(value = Role.class, mode = EnumSource.Mode.EXCLUDE, names = {"INDUSTRY_SUBMIT", "EXPLORATION", "MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM"})
  void getWorkArea_whenDoesNotHavePermission_assertCanCreateSubmissionsFalse(Role role) throws Exception {
    List<WorkAreaItem> workAreaItems = List.of();
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);
    var teamRole = TeamRoleTestUtil.Builder().withRole(role).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/workarea/workArea"))
        .andExpect(model().attribute("canCreateSubmissions", false));
  }

  @ParameterizedTest
  @EnumSource(value = Role.class)
  void getWorkArea_assertMonitoring(Role role) throws Exception {
    List<WorkAreaItem> workAreaItems = List.of();
    when(workAreaItemService.getWorkAreaItems(USER)).thenReturn(workAreaItems);
    var teamRole = TeamRoleTestUtil.Builder().withRole(role).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/workarea/workArea"));

    verify(timer).record(any(Long.class), eq(TimeUnit.MILLISECONDS));
  }
}
