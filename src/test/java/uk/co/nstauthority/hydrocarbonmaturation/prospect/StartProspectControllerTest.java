package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = StartProspectController.class)
class StartProspectControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  GeneralDetailsFormService generalDetailsFormService;

  @MockitoBean
  SubmissionCreateService submissionCreateService;

  @SecurityTest
  void getStartPage_assertStatusOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
        get(ReverseRouter.route(on(StartProspectController.class).getStartPage()))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void startProspect_assertRedirection() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(StartProspectController.class).startProspect()))
            .with(user(USER))
            .with(csrf()))
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl(ReverseRouter.route(on(NewProspectController.class)
        .getNewProspectPage(null, null))));
  }

  @Test
  void getStartPage_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartProspectController.class).getStartPage()))
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();
    assertEquals(ReverseRouter.route(on(StartProspectController.class).startProspect()), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm(null)), model.get("backLinkUrl"));
  }

  @SecurityTest
  void startProspect_whenNoPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartProspectController.class).startProspect()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

  }

  @SecurityTest
  void getStartPage_whenNoPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(StartProspectController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}