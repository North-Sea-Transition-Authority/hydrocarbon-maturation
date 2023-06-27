package uk.co.nstauthority.hydrocarbonmaturation.field;

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

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = StartFieldController.class)
class StartFieldControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  GeneralDetailsFormService generalDetailsFormService;

  @SecurityTest
  void getStartPage_assertStatusOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartFieldController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void startField_assertRedirection() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(StartFieldController.class).startField()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(NewFieldController.class)
            .getNewFieldPage(null))));
  }

  @Test
  void getStartPage_assertModelProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(StartFieldController.class).getStartPage()))
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();
    assertEquals(ReverseRouter.route(on(StartFieldController.class).startField()), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(FieldPurposeController.class).getFieldPurposeForm()), model.get("backLinkUrl"));
  }

  @SecurityTest
  void startField_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(StartFieldController.class).startField()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

  }

  @SecurityTest
  void getStartPage_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartFieldController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}