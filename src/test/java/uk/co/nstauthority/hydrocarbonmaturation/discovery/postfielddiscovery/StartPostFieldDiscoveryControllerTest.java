package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = StartPostFieldDiscoveryController.class)
class StartPostFieldDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  FieldStatusService fieldStatusService;

  Resource resource;
  UUID resourceId;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.FIELD);
    resourceId = resource.getId();
  }

  @SecurityTest
  void getStartPostFieldDiscoveryPage_noPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(false);

    mockMvc.perform(
        get(ReverseRouter.route(on(StartPostFieldDiscoveryController.class).getStartPostFieldDiscoveryPage(resourceId, null)))
                    .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startPostFieldDiscovery_noPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(false);

    mockMvc.perform(
        post(ReverseRouter.route(on(StartPostFieldDiscoveryController.class).startPostFieldDiscovery(resourceId, null)))
                    .with(user(USER))
                    .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startPostFieldDiscovery_correctPermissions_assertRedirection() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(StartPostFieldDiscoveryController.class).startPostFieldDiscovery(resourceId, null)))
                    .with(user(USER))
                    .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(PostFieldDiscoveryController.class)
            .getCreatePostFieldDiscoveryPage(resourceId, null))));
  }

  @Test
  void getStartPostFieldDiscoveryPage_whenShelved_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(true);

    var field = new Field();
    var fieldStatus = FieldStatus.STATUS_000;
    when(fieldPersistenceService.getFieldOrThrow(resourceId)).thenReturn(field);
    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(fieldStatus));

    mockMvc.perform(
        get(ReverseRouter.route(on(StartPostFieldDiscoveryController.class).getStartPostFieldDiscoveryPage(resourceId, null)))
                    .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("/remi/discovery/startDiscoveryFromField"))
        .andExpect(model().attribute("actionUrl", ReverseRouter.route(on(StartPostFieldDiscoveryController.class)
              .startPostFieldDiscovery(resourceId, USER))))
        .andExpect(model().attribute("backLinkUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
              .showResourceRecord(resourceId, USER))))
        .andExpect(model().attribute("fieldStatus", fieldStatus))
        .andExpect(model().attribute("guidanceText", StartPostFieldDiscoveryController.SHELVED_GUIDANCE_TEXT));
  }

  @Test
  void getStartPostFieldDiscoveryPage_whenCop_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(true);

    var field = new Field();
    var fieldStatus = FieldStatus.STATUS_900;
    when(fieldPersistenceService.getFieldOrThrow(resourceId)).thenReturn(field);
    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(fieldStatus));

    mockMvc.perform(
            get(ReverseRouter.route(on(StartPostFieldDiscoveryController.class).getStartPostFieldDiscoveryPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("/remi/discovery/startDiscoveryFromField"))
        .andExpect(model().attribute("actionUrl", ReverseRouter.route(on(StartPostFieldDiscoveryController.class)
            .startPostFieldDiscovery(resourceId, USER))))
        .andExpect(model().attribute("backLinkUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resourceId, USER))))
        .andExpect(model().attribute("fieldStatus", fieldStatus))
        .andExpect(model().attribute("guidanceText", StartPostFieldDiscoveryController.COP_GUIDANCE_TEXT));
  }

}