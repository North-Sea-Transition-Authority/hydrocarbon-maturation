package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

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
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
import uk.co.nstauthority.hydrocarbonmaturation.file.FileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@ContextConfiguration(classes = SubmissionSupportingDocumentsController.class)
class SubmissionSupportingDocumentsControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  SupportingDocumentService supportingDocumentService;

  @MockBean
  FileUploadService fileUploadService;

  private UUID submissionDetailId;
  private SupportingDocumentType supportingDocumentType;
  private UploadedFile uploadedFile;
  private MultipartFile multipartFile;

  @BeforeEach
  void setUp() throws IOException {
    submissionDetailId = submissionDetail.getId();
    uploadedFile = FileTestUtil.createValidUploadedFile();
    multipartFile = FileTestUtil.createMultipartFileMock();

    supportingDocumentType = SupportingDocumentType.MAP_OF_OPPORTUNITY;
  }

  @SecurityTest
  void upload_whenRegulatorInResponsibleTeamAndIsInitialDraft_assertStatusOk() throws Exception {
    submissionDetail.setVersionNumber(1);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", (byte[]) null);
    mockMvc.perform(multipart(ReverseRouter.route(
            on(SubmissionSupportingDocumentsController.class).upload(submissionDetailId, supportingDocumentType, null)))
            .file(mockMultipartFile)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(supportingDocumentService).processFileUpload(submissionDetail, supportingDocumentType, mockMultipartFile);
  }

  @SecurityTest
  void upload_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", (byte[]) null);
    mockMvc.perform(multipart(ReverseRouter.route(
            on(SubmissionSupportingDocumentsController.class).upload(submissionDetailId, supportingDocumentType, null)))
            .file(mockMultipartFile)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void download_whenInResponsibleOrganisation_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(supportingDocumentService.findUploadedFileOrThrow(submissionDetail, uploadedFile.getId())).thenReturn(uploadedFile);
    var fileResource = new ClassPathResource("banner.txt");
    when(fileUploadService.downloadFile(uploadedFile)).thenReturn(fileResource.getInputStream());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    var response = mockMvc.perform(
        get(ReverseRouter.route(
            on(SubmissionSupportingDocumentsController.class).download(submissionDetailId, uploadedFile.getId())))
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
  void delete_whenRegulator_andWhenLatestDraftUpdate_assertStatusOk() throws Exception {
    submissionDetail.setVersionNumber(5);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(submissionDetail,
        Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(true);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    mockMvc.perform(post(
        ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).delete(submissionDetailId, uploadedFile.getId())))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(supportingDocumentService, times(1)).deleteFile(submissionDetail, uploadedFile.getId());
  }

  @SecurityTest
  void delete_whenRegulator_andWhenNotLatestDraftUpdate_assertStatusOk() throws Exception {
    submissionDetail.setVersionNumber(5);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(submissionDetail,
        Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(false);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    mockMvc.perform(post(
            ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).delete(submissionDetailId, uploadedFile.getId())))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verifyNoInteractions(supportingDocumentService);
  }

  @SecurityTest
  void delete_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    mockMvc.perform(post(
            ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).delete(submissionDetailId, uploadedFile.getId())))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isForbidden());
  }
}