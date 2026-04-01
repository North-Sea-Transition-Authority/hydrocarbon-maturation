package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = ResourceSupportingDocumentsController.class)
class ResourceSupportingDocumentsControllerTest extends AbstractSubmissionControllerTest {
  private static final UUID RESOURCE_ID = UUID.randomUUID();
  private static final UUID FILE_ID = UUID.randomUUID();
  private static final Class<ResourceSupportingDocumentsController> CONTROLLER = ResourceSupportingDocumentsController.class;

  @MockitoBean
  private FileService fileService;

  @MockitoBean
  private ResourceService resourceService;

  @MockitoBean
  private ResourceSupportingDocumentService resourceSupportingDocumentService;
  private Resource resource;

  @Test
  void download() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

    var uploadedFile = new UploadedFile();
    when(fileService.find(FILE_ID)).thenReturn(Optional.of(uploadedFile));

    when(fileService.download(uploadedFile)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(get(ReverseRouter.route(on(CONTROLLER)
            .download(RESOURCE_ID, FILE_ID)))
            .with(user(user)))
        .andExpect(status().isOk());

    verify(resourceSupportingDocumentService).throwIfFileDoesNotBelongToResource(uploadedFile, resource);
    verify(fileService).download(uploadedFile);
  }

  @Test
  void download_invalidFileId() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(fileService.find(FILE_ID)).thenReturn(Optional.empty());
    when(resourceSupportingDocumentService.getFileNotFoundException(FILE_ID, resource))
        .thenReturn(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(get(ReverseRouter.route(on(CONTROLLER)
            .download(RESOURCE_ID, FILE_ID)))
            .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void download_fileNotLinkedToApplication() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

    var uploadedFile = new UploadedFile();
    when(fileService.find(FILE_ID)).thenReturn(Optional.of(uploadedFile));

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
        .when(resourceSupportingDocumentService)
        .throwIfFileDoesNotBelongToResource(uploadedFile, resource);

    mockMvc.perform(get(ReverseRouter.route(on(CONTROLLER)
            .download(RESOURCE_ID, FILE_ID)))
            .with(user(user)))
        .andExpect(status().isNotFound());

    verify(resourceSupportingDocumentService).throwIfFileDoesNotBelongToResource(uploadedFile, resource);
  }

  @SecurityTest
  void download_andHasNoPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    mockMvc.perform(get(
            ReverseRouter.route(on(ResourceSupportingDocumentsController.class).download(RESOURCE_ID, FILE_ID)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}