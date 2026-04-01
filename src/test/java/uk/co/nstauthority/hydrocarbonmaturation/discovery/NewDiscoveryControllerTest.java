package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.NewResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;


@ContextConfiguration(classes = NewDiscoveryController.class)
class NewDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockitoBean
  private NewResourceDetailsFormValidator newResourceDetailsFormValidator;

  @MockitoBean
  private SubmissionCreateService submissionCreateService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getNewDiscoveryPage_assertModelProperties() throws Exception {
    var form = new ResourceDetailsForm();
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    form.setResponsibleOrganisationId("2523");
    var selectedOranisationMap = Map.of("2523", "Org name");
    when(organisationUnitQueryService.getPreSelectedOrganisationUnit(2523))
        .thenReturn(selectedOranisationMap);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(NewDiscoveryController.class).getNewDiscoveryPage(null, null)))
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
            "onshoreOffshore",
            "alreadySelectedOrganisation")
        .containsExactly(
            ReverseRouter.route(on(StartDiscoveryController.class).getStartPage()),
            ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null, null)),
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)),
            form,
            ResourceType.DISCOVERY.getDisplayName().toLowerCase(),
            true,
            OnshoreOffshore.getOnshoreOffshore(),
            selectedOranisationMap);
  }

  @SecurityTest
  void createDiscovery_asValidForm_assertRedirect() throws Exception {
    var form = new ResourceDetailsForm();
    when(newResourceDetailsFormValidator.isValid(any(), any(), any())).thenReturn(true);
    when(submissionCreateService.createDiscovery(form, USER)).thenReturn(submissionDetail);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId, null))));
  }

  @SecurityTest
  void createDiscovery_asInvalidForm_assertIsOk() throws Exception {
    var form = new ResourceDetailsForm();

    when(newResourceDetailsFormValidator.isValid(any(), any(), any())).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null, null)))
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
            ReverseRouter.route(on(NewDiscoveryController.class).createDiscovery(null, null, null)),
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)),
            form,
            ResourceType.DISCOVERY.getDisplayName().toLowerCase(),
            true,
            OnshoreOffshore.getOnshoreOffshore());

    verifyNoInteractions(submissionCreateService);
  }

  @SecurityTest
  void createDiscovery_hasWrongPermissions_assertIsForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(NewDiscoveryController.class)
                .createDiscovery(null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getNewDiscoveryPage_hasWrongPermissions_assertIsForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewDiscoveryController.class)
                .getNewDiscoveryPage(null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}