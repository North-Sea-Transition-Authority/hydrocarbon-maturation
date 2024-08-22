package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = StartDiscoveryController.class)
class StartDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private StartDiscoveryService startDiscoveryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @SecurityTest
  void getStartPage_assertStatusOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void getStartPage_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("/remi/discovery/newDiscovery"))
        .andExpect(model().attribute("actionUrl", ReverseRouter.route(on(StartDiscoveryController.class)
            .startDiscovery())))
        .andExpect(model().attribute("backLinkUrl", ReverseRouter.route(on(DiscoveryPurposeController.class)
            .getDiscoveryPurposeForm(null))));
  }

  @SecurityTest
  void startDiscovery() throws Exception {
    when(startDiscoveryService.createNewDiscovery(any(), any())).thenReturn(submissionDetail);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(NewDiscoveryController.class)
            .getNewDiscoveryPage( null, null))));
  }

  @SecurityTest
  void startDiscovery_whenNoPermission() throws Exception {
    when(startDiscoveryService.createNewDiscovery(any(), any())).thenReturn(submissionDetail);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getStartPage_whenNoPermission() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}