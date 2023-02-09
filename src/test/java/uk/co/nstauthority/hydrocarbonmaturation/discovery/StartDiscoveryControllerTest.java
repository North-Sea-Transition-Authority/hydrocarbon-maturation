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

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = StartDiscoveryController.class)
class StartDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private StartDiscoveryService startDiscoveryService;

  private SubmissionDetail submissionDetail;
  private ResourceDetailsForm form;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    form = new ResourceDetailsForm();
  }

  @SecurityTest
  void getStartPage_assertStatusOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void getStartPage_assertModelProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("/remi/discovery/newDiscovery"))
        .andExpect(model().attribute("actionUrl", ReverseRouter.route(on(StartDiscoveryController.class)
            .startDiscovery())))
        .andExpect(model().attribute("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class)
            .getSubmissionPurpose())));
  }

  @SecurityTest
  void startDiscovery() throws Exception {
    when(startDiscoveryService.createNewDiscovery(any())).thenReturn(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(NewDiscoveryController.class)
            .getNewDiscoveryPage( null))));
  }

  @SecurityTest
  void startDiscovery_whenNoPermission() throws Exception {
    when(startDiscoveryService.createNewDiscovery(any())).thenReturn(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery()))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getStartPage_whenNoPermission() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}