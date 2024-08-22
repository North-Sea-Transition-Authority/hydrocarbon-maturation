package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = UpdateDiscoveryController.class)
class UpdateDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private UpdateDiscoveryService updateDiscoveryService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getUpdateDiscoveryPage_assertModelProperties() throws Exception {
    var form = new UpdateDiscoveryForm();
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(UpdateDiscoveryController.class).getUpdateDiscoveryPage(null)))
                .flashAttr("form", form)
                .with(user(USER))
        )
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "form",
            "discoveryApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class).getStartUpdateDiscoveryPage()),
            ReverseRouter.route(on(UpdateDiscoveryController.class).createUpdateDiscovery(null, null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class).searchDiscoveries(null, SubmissionType.UPDATE_DISCOVERY, null))
        );
  }

  @SecurityTest
  void createUpdateDiscovery_asValidForm_assertRedirect() throws Exception {
    var form = new UpdateDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateDiscoveryForm.class, "form");
    when(updateDiscoveryService.validate(any(), any())).thenReturn(bindingResult);
    when(updateDiscoveryService.createUpdateDiscovery(form, USER)).thenReturn(submissionDetail);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(UpdateDiscoveryController.class).createUpdateDiscovery(null, null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

    verify(updateDiscoveryService, times(1)).createUpdateDiscovery(any(UpdateDiscoveryForm.class), eq(USER));
  }

  @SecurityTest
  void createUpdateDiscovery_asInvalidForm_assertIsOk() throws Exception {
    var form = new UpdateDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateDiscoveryForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(updateDiscoveryService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(UpdateDiscoveryController.class).createUpdateDiscovery(null, null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "form",
            "discoveryApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class).getStartUpdateDiscoveryPage()),
            ReverseRouter.route(on(UpdateDiscoveryController.class).createUpdateDiscovery(null, null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchDiscoveries(null, SubmissionType.UPDATE_DISCOVERY, null))
        );

    verify(updateDiscoveryService, times(1)).validate(any(), any());
    verify(updateDiscoveryService, never()).createUpdateDiscovery(any(), any());
  }

  @SecurityTest
  void createUpdateDiscovery_whenNoPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(UpdateDiscoveryController.class).createUpdateDiscovery(null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getUpdateDiscoveryPage_whenNoPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(UpdateDiscoveryController.class).getUpdateDiscoveryPage(null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}