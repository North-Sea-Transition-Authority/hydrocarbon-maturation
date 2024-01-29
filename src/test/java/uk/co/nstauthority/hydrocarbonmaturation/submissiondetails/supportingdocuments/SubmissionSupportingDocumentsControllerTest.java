package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = SubmissionSupportingDocumentsController.class)
class SubmissionSupportingDocumentsControllerTest extends AbstractSubmissionControllerTest {
  private static final UUID FILE_ID = UUID.randomUUID();
  private static final Class<SubmissionSupportingDocumentsController> CONTROLLER = SubmissionSupportingDocumentsController.class;

  @MockBean
  private FileService fileService;

  @MockBean
  private SubmissionSupportingDocumentService supportingDocumentService;

  @Test
  void download() throws Exception {

    var uploadedFile = new UploadedFile();
    when(fileService.find(FILE_ID)).thenReturn(Optional.of(uploadedFile));

    when(fileService.download(uploadedFile)).thenReturn(ResponseEntity.ok().build());

    mockMvc.perform(get(ReverseRouter.route(on(CONTROLLER)
            .download(SUBMISSION_DETAIL_ID, FILE_ID)))
            .with(user(USER)))
        .andExpect(status().isOk());

    verify(supportingDocumentService).throwIfFileDoesNotBelongToSubmissionDetail(uploadedFile, submissionDetail);
    verify(fileService).download(uploadedFile);
  }

  @Test
  void download_invalidFileId() throws Exception {
    when(fileService.find(FILE_ID)).thenReturn(Optional.empty());
    when(supportingDocumentService.getFileNotFoundException(FILE_ID, submissionDetail))
        .thenReturn(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(get(ReverseRouter.route(on(CONTROLLER)
            .download(SUBMISSION_DETAIL_ID, FILE_ID)))
            .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void download_fileNotLinkedToApplication() throws Exception {

    var uploadedFile = new UploadedFile();
    when(fileService.find(FILE_ID)).thenReturn(Optional.of(uploadedFile));

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
        .when(supportingDocumentService)
        .throwIfFileDoesNotBelongToSubmissionDetail(uploadedFile, submissionDetail);

    mockMvc.perform(get(ReverseRouter.route(on(CONTROLLER)
            .download(SUBMISSION_DETAIL_ID, FILE_ID)))
            .with(user(USER)))
        .andExpect(status().isNotFound());

    verify(supportingDocumentService).throwIfFileDoesNotBelongToSubmissionDetail(uploadedFile, submissionDetail);
  }

  @SecurityTest
  void download_andHasNoPermission_assertForbidden() throws Exception {

    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(get(
            ReverseRouter.route(on(CONTROLLER).download(SUBMISSION_DETAIL_ID, FILE_ID)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}