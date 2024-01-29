package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class SubmissionSupportingDocumentServiceTest {
  @Mock
  private FileService fileService;

  @Mock
  private FileDocumentService fileDocumentService;
  @InjectMocks
  private SubmissionSupportingDocumentService submissionSupportingDocumentService;

  private final String USAGE_TYPE = SubmissionDetail.class.getSimpleName();
  private static final UUID FILE_ID = UUID.randomUUID();
  private static final FileDocumentType DOCUMENT_TYPE = FileDocumentType.GEOLOGICAL_CROSS_SECTION;
  private static final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final List<UploadedFileForm> EXISTING_FILES = Collections.emptyList();

  @Test
  void saveDocuments_VerifyMethodCall() {

    submissionSupportingDocumentService.saveDocuments(SUBMISSION_DETAIL, EXISTING_FILES, DOCUMENT_TYPE);

    verify(fileDocumentService).saveDocuments(EXISTING_FILES, DOCUMENT_TYPE, SUBMISSION_DETAIL.getId().toString(), USAGE_TYPE);
  }

  @Test
  void getUploadedFiles() {
    var uploadedFiles = Collections.<UploadedFile>emptyList();
    when(submissionSupportingDocumentService.getUploadedFiles(SUBMISSION_DETAIL, DOCUMENT_TYPE))
        .thenReturn(uploadedFiles);

    assertThat(submissionSupportingDocumentService.getUploadedFiles(SUBMISSION_DETAIL, DOCUMENT_TYPE))
        .isEqualTo(uploadedFiles);
  }

  @Test
  void getFileNotFoundException_VerifyMethodCall() {
    submissionSupportingDocumentService.getFileNotFoundException(FILE_ID, SUBMISSION_DETAIL);

    verify(fileDocumentService).getFileNotFoundException(FILE_ID, USAGE_TYPE, SUBMISSION_DETAIL.getId().toString());
  }

  @Test
  void throwIfFileDoesNotBelongToSubmissionDetail_VerifyMethodCall() {
    var uploadedFile = new UploadedFile();
    submissionSupportingDocumentService.throwIfFileDoesNotBelongToSubmissionDetail(uploadedFile, SUBMISSION_DETAIL);

    verify(fileDocumentService).throwIfFileDoesNotBelongToUsageType(uploadedFile, null, SUBMISSION_DETAIL.getId().toString(), USAGE_TYPE);
  }

  @Test
  void fileUploadComponentAttributes_VerifyMethodCall() {
    submissionSupportingDocumentService.fileUploadComponentAttributes(EXISTING_FILES, DOCUMENT_TYPE);

    verify(fileDocumentService).fileUploadComponentAttributes(EXISTING_FILES, DOCUMENT_TYPE);
  }

  @Test
  void copyUploadedFiles() {

    var newSubmissionDetail = new SubmissionDetail();
    var uploadedFile1 = mock(UploadedFile.class);
    var uploadedFile2 = mock(UploadedFile.class);
    var uploadedFiles = List.of(uploadedFile1, uploadedFile2);

    when(submissionSupportingDocumentService.getUploadedFiles(SUBMISSION_DETAIL, DOCUMENT_TYPE)).thenReturn(uploadedFiles);

    submissionSupportingDocumentService.copyUploadedFiles(SUBMISSION_DETAIL, newSubmissionDetail, DOCUMENT_TYPE);

    // Verify that the expected methods were called
    verify(fileService, times(1)).copy(eq(uploadedFile1), any());
    verify(fileService, times(1)).copy(eq(uploadedFile2), any());
  }

  @Test
  void deleteFile_FileFound_FileDeleted() {
    var uploadedFile = new UploadedFile();
    when(fileService.find(FILE_ID)).thenReturn(Optional.of(uploadedFile));

    submissionSupportingDocumentService.deleteFile(FILE_ID);

    verify(fileService).delete(uploadedFile);
  }

  @Test
  void deleteFile_FileNotFound_NothingHappens() {
    when(fileService.find(FILE_ID)).thenReturn(Optional.empty());

    submissionSupportingDocumentService.deleteFile(FILE_ID);

    verify(fileService, never()).delete(any());
  }
}