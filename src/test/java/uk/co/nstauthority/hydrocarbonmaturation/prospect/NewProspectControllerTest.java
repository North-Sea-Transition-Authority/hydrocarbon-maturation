package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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


@ContextConfiguration(classes = NewProspectController.class)
class NewProspectControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockitoBean
  private SubmissionCreateService submissionCreateService;

  @MockitoBean
  private NewResourceDetailsFormValidator newResourceDetailsFormValidator;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getNewProspectPage_withPreSelectedOrganisation_assertModelProperties() throws Exception {
    var form = new ResourceDetailsForm();
    form.setResponsibleOrganisationId("191002");

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var alreadySelectedOrgMap = mock(Map.class);
    when(organisationUnitQueryService.getPreSelectedOrganisationUnit(191002))
        .thenReturn(alreadySelectedOrgMap);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(NewProspectController.class).getNewProspectPage(null, null)))
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
            ReverseRouter.route(on(StartProspectController.class).getStartPage()),
            ReverseRouter.route(on(NewProspectController.class).createProspect(null, null, null)),
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)),
            form,
            ResourceType.PROSPECT.getDisplayName().toLowerCase(),
            true,
            OnshoreOffshore.getOnshoreOffshore(),
            alreadySelectedOrgMap);
  }

  @Test
  void getNewProspectPage_withoutPreSelectedOrganisation_assertModelProperties() throws Exception {
    var form = new ResourceDetailsForm();
    form.setResponsibleOrganisationId(null);

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var alreadySelectedOrgMap = mock(Map.class);
    when(organisationUnitQueryService.getPreSelectedOrganisationUnit(null))
        .thenReturn(alreadySelectedOrgMap);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(NewProspectController.class).getNewProspectPage(null, null)))
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
            ReverseRouter.route(on(StartProspectController.class).getStartPage()),
            ReverseRouter.route(on(NewProspectController.class).createProspect(null, null, null)),
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)),
            form,
            ResourceType.PROSPECT.getDisplayName().toLowerCase(),
            true,
            OnshoreOffshore.getOnshoreOffshore(),
            alreadySelectedOrgMap);
  }

  @SecurityTest
  void createProspect_asValidForm_assertRedirect() throws Exception {
    var form = new ResourceDetailsForm();
    when(newResourceDetailsFormValidator.isValid(any(), any(), any())).thenReturn(true);
    when(submissionCreateService.createInitialProspect(form, USER)).thenReturn(submissionDetail);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(NewProspectController.class).createProspect(null, null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId, null))));
  }

  @SecurityTest
  void createProspect_asInvalidForm_assertIsOk() throws Exception {
    var form = new ResourceDetailsForm();
    when(newResourceDetailsFormValidator.isValid(any(), any(), any())).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var alreadySelectedOrgMap = mock(Map.class);
    when(organisationUnitQueryService.getPreSelectedOrganisationUnit(any()))
        .thenReturn(alreadySelectedOrgMap);

    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(NewProspectController.class).createProspect(null, null, null)))
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
            "onshoreOffshore",
            "alreadySelectedOrganisation")
        .containsExactly(
            ReverseRouter.route(on(StartProspectController.class).getStartPage()),
            ReverseRouter.route(on(NewProspectController.class).createProspect(null, null, null)),
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)),
            form,
            ResourceType.PROSPECT.getDisplayName().toLowerCase(),
            true,
            OnshoreOffshore.getOnshoreOffshore(),
            alreadySelectedOrgMap);

    verifyNoInteractions(submissionCreateService);
  }

  @SecurityTest
  void createProspect_hasNoPermission_assertIsForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(NewProspectController.class)
                .createProspect(null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getNewProspectPage_hasNoPermission_assertIsForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewProspectController.class)
                .getNewProspectPage(null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}