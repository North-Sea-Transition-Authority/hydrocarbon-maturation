package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = ResourceSupportingDocumentsController.class)
class ResourceSupportingDocumentsControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  ResourceSupportingDocumentService resourceSupportingDocumentService;

  @MockBean
  ResourceService resourceService;

  @MockBean
  FileUploadService fileUploadService;

  private UUID resourceId;
  private ResourceSupportingDocumentType resourceSupportingDocumentType;
  private UploadedFile uploadedFile;
  private MultipartFile multipartFile;
  private Resource resource;

  @BeforeEach
  void setUp() throws IOException {
    resource = new Resource();
    resource.setId(UUID.randomUUID());
    resourceId = resource.getId();
    uploadedFile = FileTestUtil.createValidUploadedFile();
    multipartFile = FileTestUtil.createMultipartFileMock();

    resourceSupportingDocumentType = ResourceSupportingDocumentType.RELINQUISHMENT_REPORT;
  }

  @Test
  void upload_whenInvalidResourceId_assertStatus() throws Exception{
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());

    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", (byte[]) null);
    mockMvc.perform(multipart(ReverseRouter.route(
            on(ResourceSupportingDocumentsController.class)
                .upload(resourceId, resourceSupportingDocumentType, null)))
            .file(mockMultipartFile)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is4xxClientError());

    verifyNoInteractions(resourceSupportingDocumentService);
  }

  @Test
  void delete_whenInvalidResourceId_assertStatus() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());
    mockMvc.perform(post(
            ReverseRouter.route(on(ResourceSupportingDocumentsController.class).delete(resourceId, uploadedFile.getId())))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is4xxClientError());

    verifyNoInteractions(resourceSupportingDocumentService);
  }

  @Test
  void download_whenInvalidResourceId_assertStatusOk() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());
    mockMvc.perform(
            get(ReverseRouter.route(
                on(ResourceSupportingDocumentsController.class).download(resourceId, uploadedFile.getId())))
                .with(user(USER)))
        .andExpect(status().is4xxClientError());
    verifyNoInteractions(fileUploadService);
    verifyNoInteractions(resourceSupportingDocumentService);
  }

  @SecurityTest
  void upload_assertStatusOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));

    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", (byte[]) null);
    mockMvc.perform(multipart(ReverseRouter.route(
            on(ResourceSupportingDocumentsController.class).upload(resourceId, resourceSupportingDocumentType, null)))
            .file(mockMultipartFile)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(resourceSupportingDocumentService).processFileUpload(resource, resourceSupportingDocumentType, mockMultipartFile);
  }

  @SecurityTest
  void download_assertStatusOk() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(resourceSupportingDocumentService.findUploadedFileOrThrow(resource, uploadedFile.getId())).thenReturn(uploadedFile);
    var fileResource = new ClassPathResource("banner.txt");
    when(fileUploadService.downloadFile(uploadedFile)).thenReturn(fileResource.getInputStream());
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    var response = mockMvc.perform(
            get(ReverseRouter.route(
                on(ResourceSupportingDocumentsController.class).download(resourceId, uploadedFile.getId())))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    assertThat(response)
        .extracting(
            MockHttpServletResponse::getContentType,
            MockHttpServletResponse::getContentLengthLong,
            mockHttpServletResponse -> mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION)
        )
        .contains(
            MediaType.APPLICATION_OCTET_STREAM.toString(),
            uploadedFile.getFileSizeBytes(),
            "attachment; filename=\"%s\"".formatted(uploadedFile.getFilename())
        );
  }

  @SecurityTest
  void delete_assertStatusOk() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    mockMvc.perform(post(
            ReverseRouter.route(on(ResourceSupportingDocumentsController.class).delete(resourceId, uploadedFile.getId())))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(resourceSupportingDocumentService, times(1)).deleteFile(resource, uploadedFile.getId());
  }

  @SecurityTest
  void delete_andHasNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    mockMvc.perform(post(
            ReverseRouter.route(on(ResourceSupportingDocumentsController.class).delete(resourceId, uploadedFile.getId())))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void upload_andHasNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    mockMvc.perform(post(
            ReverseRouter.route(on(ResourceSupportingDocumentsController.class).upload(
                resourceId, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT, multipartFile)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void download_andHasNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    mockMvc.perform(get(
            ReverseRouter.route(on(ResourceSupportingDocumentsController.class).download(resourceId, uploadedFile.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}