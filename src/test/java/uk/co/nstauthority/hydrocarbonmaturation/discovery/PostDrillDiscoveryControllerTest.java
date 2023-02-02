package uk.co.nstauthority.hydrocarbonmaturation.discovery;

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

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = PostDrillDiscoveryController.class)
class PostDrillDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private StartDiscoveryService startDiscoveryService;

  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getStartDiscoveryPage_assertModelProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    var form = new StartDiscoveryForm();

    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDiscoveryController.class).getStartDiscoveryPage(null)))
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
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()),
            ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class).searchProspects(null, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        );
  }

  @SecurityTest
  void startDiscovery_asValidForm_assertRedirect() throws Exception {
    var form = new StartDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    when(startDiscoveryService.validateDiscoveryFromProspect(any(), any())).thenReturn(bindingResult);
    when(startDiscoveryService.createNewDiscoveryFromProspect(form)).thenReturn(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));
  }

  @SecurityTest
  void startDiscovery_asInvalidForm_assertIsOk() throws Exception {
    var form = new StartDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(startDiscoveryService.validateDiscoveryFromProspect(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)))
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
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()),
            ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class).searchProspects(null, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        );

    verify(startDiscoveryService, times(1)).validateDiscoveryFromProspect(any(), any());
    verify(startDiscoveryService, never()).createNewDiscoveryFromProspect(any());
  }

  @SecurityTest
  void startDiscovery_whenNoPermission_assertIsForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getStartDiscoveryPage_whenNoPermission_assertIsForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(PostDrillDiscoveryController.class)
                .getStartDiscoveryPage(null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}