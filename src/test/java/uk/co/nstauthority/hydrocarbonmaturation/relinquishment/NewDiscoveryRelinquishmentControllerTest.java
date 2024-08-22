package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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


@ContextConfiguration(classes = NewDiscoveryRelinquishmentController.class)
class NewDiscoveryRelinquishmentControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private DiscoveryRelinquishmentService discoveryRelinquishmentService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getRelinquishmentPage_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class).getNewDiscoveryRelinquishmentPage()))
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "discoveryApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).getStartDiscoveryRelinquishmentPage()),
            ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class)
                .createNewDiscoveryRelinquishmentSubmission(null, null, null)),
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchDiscoveries(null, SubmissionType.RELINQUISHMENT_DISCOVERY, null)));
  }

  @SecurityTest
  void createNewRelinquishmentSubmission_asValidForm_assertRedirect() throws Exception {
    var form = new DiscoveryRelinquishmentForm();

    var bindingResult = new BeanPropertyBindingResult(ProspectRelinquishmentForm.class, "form");
    when(discoveryRelinquishmentService.validate(any(), any())).thenReturn(bindingResult);
    when(discoveryRelinquishmentService.createDiscoveryRelinquishment(form, USER)).thenReturn(submissionDetail);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class)
                .createNewDiscoveryRelinquishmentSubmission(null, null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId, null))));
  }

  @SecurityTest
  void createNewRelinquishmentSubmission_asInvalidForm_assertIsOk() throws Exception {
    var form = new DiscoveryRelinquishmentForm();

    var bindingResult = new BeanPropertyBindingResult(ProspectRelinquishmentForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(discoveryRelinquishmentService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class)
                .createNewDiscoveryRelinquishmentSubmission(null, null, null)))
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
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).getStartDiscoveryRelinquishmentPage()),
            ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class)
                .createNewDiscoveryRelinquishmentSubmission(null, null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchDiscoveries(null, SubmissionType.RELINQUISHMENT_DISCOVERY, null))
        );

    verify(discoveryRelinquishmentService, times(1)).validate(any(), any());
    verify(discoveryRelinquishmentService, never()).createDiscoveryRelinquishment(any(), any());
  }

  @SecurityTest
  void saveRegulatorManagedInformation_isNotCorrectRole_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);


    mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class)
                .getNewDiscoveryRelinquishmentPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());

  }
}