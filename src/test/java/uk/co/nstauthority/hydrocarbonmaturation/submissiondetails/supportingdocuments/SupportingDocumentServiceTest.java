package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.DeleteOutcome;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadErrorType;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class SupportingDocumentServiceTest {

  @Mock
  private FileUploadService fileUploadService;
  @Mock
  private FileUploadValidationService fileUploadValidationService;
  @Mock
  private SupportingDocumentRepository supportingDocumentRepository;

  private SupportingDocumentService supportingDocumentService;
  private static final String filename = FileTestUtil.VALID_FILENAME;
  private static final long fileSize = FileTestUtil.VALID_FILE_SIZE;
  private static final String contentType = FileTestUtil.VALID_CONTENT_TYPE;
  private static final VirtualFolder virtualFolder = FileTestUtil.VALID_VIRTUAL_FOLDER;
  private static final UploadedFile uploadedFile = FileTestUtil.createValidUploadedFile();
  private static MultipartFile multipartFile;

  @BeforeAll
  static void beforeAll() throws IOException {
    multipartFile = FileTestUtil.createMultipartFileMock();
  }

  @BeforeEach
  void setUp() {
    supportingDocumentService = new SupportingDocumentService(fileUploadService,
        fileUploadValidationService, supportingDocumentRepository);
  }

  @ParameterizedTest
  @EnumSource(
      value = UploadErrorType.class
  )
  void processFileUpload_FailsValidation_ReturnsInvalidResult(UploadErrorType uploadErrorType) {
    var submissionDetail = mock(SubmissionDetail.class);
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    when(fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename))
        .thenReturn(uploadErrorType);

    var fileUploadResult = supportingDocumentService.processFileUpload(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY, multipartFile);

    assertThat(fileUploadResult.isValid()).isFalse();
  }

  @Test
  void processFileUpload_PassesValidationAndUploadsFile_VerifyInteractions() {
    var submissionDetail = mock(SubmissionDetail.class);
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    doReturn(uploadedFile)
        .when(fileUploadService)
        .uploadFileAndSaveRecord(virtualFolder, multipartFile, fileSize, filename, contentType);

    var fileUploadResult = supportingDocumentService.processFileUpload(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY, multipartFile);

    assertThat(fileUploadResult.isValid()).isTrue();
  }

  @Test
  void deleteFile_FileDeleted_ReturnsValidResult() {
    var uuid = UUID.randomUUID();
    when(fileUploadService.findUploadedFileOrThrow(uuid)).thenReturn(uploadedFile);
    when(fileUploadService.deleteFile(uploadedFile)).thenReturn(DeleteOutcome.SUCCESS);

    var fileDeleteResult = supportingDocumentService.deleteFile(uuid);

    assertThat(fileDeleteResult.isValid()).isTrue();
  }

  @Test
  void deleteFile_DeleteFileFailed_ReturnsNotValidResult() {
    var uuid = UUID.randomUUID();
    when(fileUploadService.findUploadedFileOrThrow(uuid)).thenReturn(uploadedFile);
    when(fileUploadService.deleteFile(uploadedFile)).thenReturn(DeleteOutcome.INTERNAL_SERVER_ERROR);

    var fileDeleteResult = supportingDocumentService.deleteFile(uuid);

    assertThat(fileDeleteResult.isValid()).isFalse();
  }
}