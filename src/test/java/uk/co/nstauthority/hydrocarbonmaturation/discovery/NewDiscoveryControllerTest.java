package uk.co.nstauthority.hydrocarbonmaturation.discovery;

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

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;


@ContextConfiguration(classes = NewDiscoveryController.class)
class NewDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private StartDiscoveryService startDiscoveryService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getNewDiscoveryPage_assertModelProperties() throws Exception {
    var form = new ResourceDetailsForm();
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(regulatorTeamService.isRegulatorUser(any())).thenReturn(true);
    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(NewDiscoveryController.class).getNewDiscoveryPage(null)))
                .flashAttr("form", form)
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "organisationUnitEndpoint",
            "form",
            "resourceType",
            "isRegulatorOrAreaTeamUser",
            "onshoreOffshore")
        .containsExactly(
            ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()),
            ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null)),
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)),
            form,
            ResourceType.DISCOVERY.getDisplayName().toLowerCase(),
            true,
            OnshoreOffshore.getOnshoreOffshore());
  }

  @SecurityTest
  void createDiscovery_asValidForm_assertRedirect() throws Exception {
    var form = new ResourceDetailsForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateDiscoveryForm.class, "form");
    when(startDiscoveryService.validateNewDiscovery(any(), any())).thenReturn(bindingResult);
    when(startDiscoveryService.createNewDiscovery(form)).thenReturn(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId))));
  }

  @SecurityTest
  void createDiscovery_asInvalidForm_assertIsOk() throws Exception {
    var form = new ResourceDetailsForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateDiscoveryForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(startDiscoveryService.validateNewDiscovery(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(regulatorTeamService.isRegulatorUser(any())).thenReturn(true);

    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null)))
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
            "organisationUnitEndpoint",
            "form",
            "resourceType",
            "isRegulatorOrAreaTeamUser",
            "onshoreOffshore")
        .containsExactly(
            ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()),
            ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null)),
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)),
            form,
            ResourceType.DISCOVERY.getDisplayName().toLowerCase(),
            true,
            OnshoreOffshore.getOnshoreOffshore());

    verify(startDiscoveryService, times(1)).validateNewDiscovery(eq(form), any());
    verify(startDiscoveryService, never()).createNewDiscovery(any());
  }

  @SecurityTest
  void createDiscovery_hasNoPermission_assertIsForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(NewDiscoveryController.class)
                .createDiscovery(null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getNewDiscoveryPage_hasNoPermission_assertIsForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryController.class)
                .getNewDiscoveryPage(null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}