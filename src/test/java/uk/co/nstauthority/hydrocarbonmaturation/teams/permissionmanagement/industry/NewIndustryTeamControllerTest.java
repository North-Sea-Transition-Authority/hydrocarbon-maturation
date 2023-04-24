package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import java.util.Optional;
import java.util.Set;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = NewIndustryTeamController.class)
public class NewIndustryTeamControllerTest extends AbstractControllerTest {

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private NewTeamFormValidator newTeamFormValidator;

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @MockBean
  private PermissionService permissionService;

  @SecurityTest
  void renderNewTeamForm_authorisedUser_isOk() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(permissionService.hasPermission(user, Set.of(RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS))).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(NewIndustryTeamController.class).renderNewIndustryTeamForm(new NewTeamForm())))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/addTeam"))
    ;
  }

  @SecurityTest
  void addNewIndustryTeam_FormValidates_RedirectToTeamMemberList() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().withWuaId(100L).build();
    when(permissionService.hasPermission(user, Set.of(RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS))).thenReturn(true);

    var organisationGroupId = 10000;
    var orgGroup = OrganisationGroupTestUtil.createOrganisationGroupDto(organisationGroupId, "Royal Dutch Shell");

    var form = new NewTeamForm();
    form.setOrganisationGroupId(String.valueOf(organisationGroupId));

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    when(industryTeamService.getTeamByOrganisationGroupId((organisationGroupId)))
        .thenReturn(Optional.of(team));

    when(organisationGroupQueryService.getOrganisationGroupById(any())).thenReturn(Optional.of(orgGroup));

    mockMvc.perform(
            post(ReverseRouter.route(on(NewIndustryTeamController.class).addNewIndustryTeam(form, emptyBindingResult(), null)))
                .flashAttr("form", form)
                .with(csrf())
                .with(user(user)))
        .andExpect(status().is3xxRedirection());
  }

  @SecurityTest
  void addNewIndustryTeam_FormValidatesGroupDoesntExist_RedirectToTeamMemberList() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().withWuaId(100L).build();
    when(permissionService.hasPermission(user, Set.of(RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS))).thenReturn(true);

    var organisationGroupId = 10000;
    var orgGroup = OrganisationGroupTestUtil.createOrganisationGroupDto(organisationGroupId, "Royal Dutch Shell");

    when(organisationGroupQueryService.getOrganisationGroupById(any())).thenReturn(Optional.of(orgGroup));

    var form = new NewTeamForm();
    form.setOrganisationGroupId(String.valueOf(organisationGroupId));

    when(industryTeamService.getTeamByOrganisationGroupId((organisationGroupId)))
        .thenReturn(Optional.empty());

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    when(industryTeamService.createTeam(anyString(), anyInt())).thenReturn(team);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewIndustryTeamController.class).addNewIndustryTeam(form, emptyBindingResult(), null)))
                .flashAttr("form", form)
                .with(csrf())
                .with(user(user)))
        .andExpect(status().is3xxRedirection());
  }
}
