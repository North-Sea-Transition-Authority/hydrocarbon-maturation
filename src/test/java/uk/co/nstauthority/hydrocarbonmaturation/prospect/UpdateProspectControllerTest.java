package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

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
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.StartUpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;


@ContextConfiguration(classes = UpdateProspectController.class)
class UpdateProspectControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private UpdateProspectService updateProspectService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getUpdatePage_assertModelProperties() throws Exception {
    var form = new UpdateProspectForm();
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(UpdateProspectController.class).getUpdatePage(null)))
            .flashAttr("form", form)
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "informationSources",
            "form",
            "prospectApiEndpoint")
        .containsExactly(ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage()),
            ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, emptyBindingResult(), null)),
            InformationSource.getInformationSource(),
            form,
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.PROSPECT_UPDATE, null)));
  }

  @SecurityTest
  void updateProspect_asValidForm_assertRedirect() throws Exception {
    var form = new UpdateProspectForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    when(updateProspectService.validate(any(), any())).thenReturn(bindingResult);
    when(updateProspectService.createUpdateProspect(form, USER)).thenReturn(submissionDetail);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId, null))));
  }

  @SecurityTest
  void updateProspect_asInvalidForm_assertIsOk() throws Exception {
    var form = new UpdateProspectForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(updateProspectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    var modelAndView = mockMvc.perform(
        post(ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, null, null)))
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
            "informationSources",
            "form",
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage()),
            ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, emptyBindingResult(), null)),
            InformationSource.getInformationSource(),
            form,
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.PROSPECT_UPDATE, null))
        );

    verify(updateProspectService, times(1)).validate(any(), any());
    verify(updateProspectService, never()).createUpdateProspect(any(), any());
  }

  @SecurityTest
  void updateProspect_hasNoPermission_assertIsForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(UpdateProspectController.class)
                .updateProspect(null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getUpdatePage_hasNoPermission_assertIsForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(UpdateProspectController.class)
                .getUpdatePage(null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}