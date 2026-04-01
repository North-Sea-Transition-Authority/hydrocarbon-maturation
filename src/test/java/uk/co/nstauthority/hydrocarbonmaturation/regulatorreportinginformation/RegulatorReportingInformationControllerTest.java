package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = RegulatorReportingInformationController.class)
class RegulatorReportingInformationControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockitoBean
  private ResourceService resourceService;

  @MockitoBean
  private RegulatorReportingInformationService regulatorReportingInformationService;

  @MockitoBean
  private SubmissionResourceService submissionResourceService;

  @MockitoBean
  private RegulatorReportingInformationValidator regulatorReportingInformationValidator;

  private RegulatorReportingInformationForm form;
  private UUID resourceId;
  private Resource resource;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    resource = new Resource();
    form = new RegulatorReportingInformationForm();
    resourceId = UUID.randomUUID();
    resource.setId(resourceId);
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource);
  }

  @SecurityTest
  void getRegulatorReportingInformationPage_whenNoResource_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .renderPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void saveRegulatorReportingInformation_whenNoResource_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .submitPage(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getRegulatorReportingInformationPage_whenResourceIsNotActive_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    resource.setActive(false);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));

    mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .renderPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void saveRegulatorReportingInformation_whenResourceIsNotActive_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    resource.setActive(false);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .submitPage(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getRegulatorReportingInformationPage_whenNoApprovedSubmissionDetail_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .renderPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void saveRegulatorReportingInformation_whenNoApprovedSubmissionDetail_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .submitPage(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getRegulatorReportingInformationPage_whenNotResponsibleTeamUser_assertStatusForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);


    mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .renderPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveRegulatorReportingInformationPage_whenNotResponsibleTeamUser_assertStatusForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);


    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                .submitPage(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getRegulatorReportingInformationPage_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorReportingInformationService.getForm(resource))
        .thenReturn(form);

    var preselectedWell = Map.of("well1", "well1");
    when(regulatorReportingInformationService.getPreSelectedWell(form.getWell())).thenReturn(preselectedWell);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(RegulatorReportingInformationController.class)
                    .renderPage(resourceId, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/review/regulatorReportingInformation"))
            .andReturn()
            .getModelAndView())
        .getModelMap();
    assertEquals(RegulatorReportingInformationForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("pageName", RegulatorReportingInformationController.PAGE_TITLE),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(resource.getId(), null))),
            entry("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))),
            entry("well", preselectedWell),
            entry("hydrocarbonCase", DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class)),
            entry("opportunityTypes", DisplayableEnumOptionUtil.getDisplayableOptions(OpportunityType.class))
        )
        .containsKeys("breadcrumbsMap");
  }
}
