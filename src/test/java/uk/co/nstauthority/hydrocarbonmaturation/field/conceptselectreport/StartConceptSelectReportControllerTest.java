package uk.co.nstauthority.hydrocarbonmaturation.field.conceptselectreport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = StartConceptSelectReportController.class)
class StartConceptSelectReportControllerTest extends AbstractSubmissionControllerTest {

  @SecurityTest
  void getStartPage_assertStatusOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartConceptSelectReportController.class).getStartPage()))
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
            get(ReverseRouter.route(on(StartConceptSelectReportController.class).getStartPage()))
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();
    assertEquals(ReverseRouter.route(on(StartConceptSelectReportController.class).startConceptSelectReport()), model.get("actionUrl"));
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
            get(ReverseRouter.route(on(StartConceptSelectReportController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}