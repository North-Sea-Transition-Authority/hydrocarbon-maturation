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

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleService;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.StartPostDrillDryHoleSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = PostDrillDryHoleController.class)
class PostDrillDryHoleControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private PostDrillDryHoleService postDrillDryHoleService;

  @MockBean
  private PermissionService permissionService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getPostDrillDryHolePage_assertOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDryHoleController.class).getPostDrillDryHolePage(null)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void getPostDrillDryHolePage_assertModelProperties() throws Exception {
    var form = new PostDrillDryHoleForm();
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDryHoleController.class).getPostDrillDryHolePage(null)))
            .flashAttr("form", form)
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "form",
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage()),
            ReverseRouter.route(on(PostDrillDryHoleController.class).createPostDrillDryHole(null, emptyBindingResult())),
            form,
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.POST_DRILL_DRY_HOLE))
        );
  }

  @SecurityTest
  void createPostDrillDryHole_asValidForm_assertRedirect() throws Exception {
    var form = new PostDrillDryHoleForm();

    var bindingResult = new BeanPropertyBindingResult(PostDrillDryHoleForm.class, "form");
    when(postDrillDryHoleService.validate(any(), any())).thenReturn(bindingResult);
    when(postDrillDryHoleService.createPostDrillDryHoleSubmission(form)).thenReturn(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(PostDrillDryHoleController.class).createPostDrillDryHole(null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId))));
  }

  @SecurityTest
  void createPostDrillDryHole_asInvalidForm_assertIsOk() throws Exception {
    var form = new PostDrillDryHoleForm();

    var bindingResult = new BeanPropertyBindingResult(PostDrillDryHoleForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(postDrillDryHoleService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(PostDrillDryHoleController.class).createPostDrillDryHole(null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(postDrillDryHoleService, times(1)).validate(any(), any());
    verify(postDrillDryHoleService, never()).createPostDrillDryHoleSubmission(any());
  }

  @SecurityTest
  void createPostDrillDryHole_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(PostDrillDryHoleController.class).createPostDrillDryHole(null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startPostDrillDiscovery() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(PostDrillDryHoleController.class).getPostDrillDryHolePage(null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}