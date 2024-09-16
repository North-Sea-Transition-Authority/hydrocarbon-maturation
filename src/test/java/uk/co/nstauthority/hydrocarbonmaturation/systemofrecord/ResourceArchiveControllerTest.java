package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

@ContextConfiguration(classes = ResourceArchiveController.class)
class ResourceArchiveControllerTest extends AbstractResourceArchiveControllerTest {

  @Test
  void archiveResourceConfirmation_unarchived_assertModelProperties() throws Exception {
    setUpUserInResponsibleTeam();

    when(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
        .thenReturn(Optional.empty());

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(ResourceArchiveController.class)
                    .archiveResourceConfirmation(RESOURCE_ID, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ResourceArchiveController.ARCHIVE_QUESTION),
            entry("cancelUrl",
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID, null)))
        )
        .doesNotContainKey("cannotBeArchivedReason");

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(
            ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE
        ),
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)),
            "resource name (coi)"
        )
    );
  }

  @Test
  void archiveResourceConfirmation_unarchivedField_assertModelProperties() throws Exception {
    setUpUserInResponsibleTeam();

    resource.setType(ResourceType.FIELD);

    when(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
        .thenReturn(Optional.empty());

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(ResourceArchiveController.class)
                    .archiveResourceConfirmation(RESOURCE_ID, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ResourceArchiveController.ARCHIVE_QUESTION),
            entry("cancelUrl",
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID, null)))
        )
        .doesNotContainKey("cannotBeArchivedReason");

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(
            ReverseRouter.route(
                on(SearchFieldRecordsController.class).searchResourceRecords(null, null)),
            SearchFieldRecordsController.PAGE_TITLE
        ),
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)),
            "resource name (coi)"
        )
    );
  }

  @Test
  void archiveResourceConfirmation_cannotBeArchived_assertModelProperties() throws Exception {
    setUpUserInResponsibleTeam();

    var cannotBeArchivedMessage = "boohoo";

    when(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
        .thenReturn(Optional.of(cannotBeArchivedMessage));

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(ResourceArchiveController.class)
                    .archiveResourceConfirmation(RESOURCE_ID, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ResourceArchiveController.ARCHIVE_QUESTION),
            entry("cancelUrl",
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID, null))),
            entry("cannotBeArchivedReason", cannotBeArchivedMessage)
        );

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(
            ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE
        ),
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)),
            "resource name (coi)"
        )
    );
  }

  @SecurityTest
  void archiveResourceConfirmation_notFromResponsibleTeam() throws Exception {
    setUpUserRolesWithoutResponsibleTeam();
    setUpResourceRecords();

    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceArchiveController.class)
                .archiveResourceConfirmation(RESOURCE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void archiveResourceConfirmation_noUser() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceArchiveController.class)
                .archiveResourceConfirmation(RESOURCE_ID, null))))
        .andExpect(status().isUnauthorized());
  }



  @Test
  void archiveResource_cannotBeArchived() throws Exception {
    setUpUserInResponsibleTeam();

    when(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
        .thenReturn(Optional.of("beep beep"));

    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceArchiveController.class)
                .archiveResource(RESOURCE_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void archiveResource_validationPassed_assertRedirect() throws Exception {
    setUpUserInResponsibleTeam();

    when(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceArchiveController.class)
                .archiveResource(RESOURCE_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(RESOURCE_ID, null))));

    verify(resourceArchiveService, times(1))
        .archiveResource(resource);
  }

  @SecurityTest
  void archiveResource_notFromResponsibleTeam() throws Exception {
    setUpUserRolesWithoutResponsibleTeam();
    setUpResourceRecords();

    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceArchiveController.class)
                .archiveResource(RESOURCE_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void archiveResource_noUser() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceArchiveController.class)
                .archiveResource(RESOURCE_ID, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void unarchiveResourceConfirmation_assertModelProperties() throws Exception {
    setUpUserInResponsibleTeam();

    when(resourceService.getResourceOrThrow(RESOURCE_ID))
        .thenReturn(resource);
    when(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
        .thenReturn(Optional.empty());

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(ResourceArchiveController.class)
                    .unarchiveResourceConfirmation(RESOURCE_ID, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", ResourceArchiveController.UNARCHIVE_QUESTION),
            entry("cancelUrl",
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID, null)))
        )
        .doesNotContainKey("cannotBeArchivedReason");

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(
            ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE
        ),
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)),
            "resource name (coi)"
        )
    );
  }

  @SecurityTest
  void unarchiveResourceConfirmation_notFromResponsibleTeam() throws Exception {
    setUpUserRolesWithoutResponsibleTeam();
    setUpResourceRecords();

    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceArchiveController.class)
                .unarchiveResourceConfirmation(RESOURCE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void unarchiveResourceConfirmation_noUser() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceArchiveController.class)
                .unarchiveResourceConfirmation(RESOURCE_ID, null))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void unarchiveResource_validationPassed_assertRedirect() throws Exception {
    setUpUserInResponsibleTeam();

    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceArchiveController.class)
                .unarchiveResource(RESOURCE_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(RESOURCE_ID, null))));

    verify(resourceArchiveService, times(1))
        .unarchiveResource(resource);
  }

  @SecurityTest
  void unarchiveResource_notFromResponsibleTeam() throws Exception {
    setUpUserRolesWithoutResponsibleTeam();
    setUpResourceRecords();

    mockMvc.perform(
        post(ReverseRouter.route(on(ResourceArchiveController.class)
            .unarchiveResource(RESOURCE_ID, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void unarchiveResource_noUser() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceArchiveController.class)
                .unarchiveResource(RESOURCE_ID, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }
}