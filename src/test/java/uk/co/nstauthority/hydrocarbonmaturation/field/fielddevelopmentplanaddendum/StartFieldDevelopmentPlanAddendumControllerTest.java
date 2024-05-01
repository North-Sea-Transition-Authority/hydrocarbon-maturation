package uk.co.nstauthority.hydrocarbonmaturation.field.fielddevelopmentplanaddendum;


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
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = StartFieldDevelopmentPlanAddendumController.class)
class StartFieldDevelopmentPlanAddendumControllerTest extends AbstractSubmissionControllerTest {

  @SecurityTest
  void getStartPage_assertStatusOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartFieldDevelopmentPlanAddendumController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void getStartPage_assertModelProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(StartFieldDevelopmentPlanAddendumController.class).getStartPage()))
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();
    assertEquals(ReverseRouter.route(on(StartFieldDevelopmentPlanAddendumController.class).startFieldDevelopmentPlanAddendum()), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(FieldPurposeController.class).getFieldPurposeForm()), model.get("backLinkUrl"));
  }

  @SecurityTest
  void getStartPage_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartFieldDevelopmentPlanAddendumController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startFieldDevelopmentAddendumPlan_assertRedirection() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(StartFieldDevelopmentPlanAddendumController.class).startFieldDevelopmentPlanAddendum()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(NewFieldDevelopmentPlanAddendumController.class)
            .getNewFdpaPage(null))));
  }
}