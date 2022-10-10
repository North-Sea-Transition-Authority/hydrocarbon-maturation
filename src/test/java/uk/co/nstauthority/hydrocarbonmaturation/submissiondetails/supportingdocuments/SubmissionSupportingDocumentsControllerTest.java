package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@WithMockUser
@ContextConfiguration(classes = SubmissionSupportingDocumentsController.class)
class SubmissionSupportingDocumentsControllerTest extends AbstractControllerTest {

  @MockBean
  SupportingDocumentService supportingDocumentService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  FileUploadService fileUploadService;

  private UUID submissionDetailId;
  private SubmissionDetail submissionDetail;
  private SupportingDocumentType supportingDocumentType;
  private UploadedFile uploadedFile;
  private MultipartFile multipartFile;

  @BeforeEach
  void setUp() throws IOException {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    uploadedFile = FileTestUtil.createValidUploadedFile();
    multipartFile = FileTestUtil.createMultipartFileMock();

    supportingDocumentType = SupportingDocumentType.MAP_OF_OPPORTUNITY;
  }

  @Test
  void upload_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", (byte[]) null);
    mockMvc.perform(multipart(ReverseRouter.route(
            on(SubmissionSupportingDocumentsController.class).upload(submissionDetailId, supportingDocumentType, null)))
            .file(mockMultipartFile)
            .with(csrf()))
        .andExpect(status().isOk());

    verify(supportingDocumentService).processFileUpload(submissionDetail, supportingDocumentType, mockMultipartFile);
  }

  @Test
  void download_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(fileUploadService.findUploadedFileOrThrow(uploadedFile.getId())).thenReturn(uploadedFile);
    var fileResource = new ClassPathResource("banner.txt");
    when(fileUploadService.downloadFile(uploadedFile)).thenReturn(fileResource.getInputStream());

    var response = mockMvc.perform(
            get(ReverseRouter.route(
                on(SubmissionSupportingDocumentsController.class).download(submissionDetailId, uploadedFile.getId()))))
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

  @Test
  void delete_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(post(
            ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).delete(submissionDetailId, uploadedFile.getId())))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(supportingDocumentService, times(1)).deleteFile(uploadedFile.getId());
  }
}