package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.DeleteOutcome;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadErrorType;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class SupportingDocumentServiceTest {

  private static String filename;
  private static long fileSize;
  private static String contentType;
  @Mock
  private FileUploadService fileUploadService;
  @Mock
  private FileUploadValidationService fileUploadValidationService;
  @Mock
  private SupportingDocumentRepository supportingDocumentRepository;

  private SupportingDocumentService supportingDocumentService;
  private static MultipartFile multipartFile;
  private static UploadedFile uploadedFile;

  @BeforeAll
  static void beforeAll() throws IOException {
    uploadedFile = createValidUploadedFile();
    multipartFile = createMultipartFile();
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
    SubmissionDetail submissionDetail = mock(SubmissionDetail.class);
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    when(fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename))
        .thenReturn(uploadErrorType);

    FileUploadResult fileUploadResult = supportingDocumentService.processFileUpload(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY, multipartFile);

    assertThat(fileUploadResult.isValid()).isFalse();
  }

  @Test
  void processFileUpload_PassesValidationAndUploadsFile_VerifyInteractions() {
    SubmissionDetail submissionDetail = mock(SubmissionDetail.class);
    VirtualFolder virtualFolder = VirtualFolder.SUPPORTING_DOCUMENTS;
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    doReturn(uploadedFile)
        .when(fileUploadService)
        .uploadFileAndSaveRecord(virtualFolder, multipartFile, fileSize, filename, contentType);

    FileUploadResult fileUploadResult = supportingDocumentService.processFileUpload(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY, multipartFile);

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

  private static UploadedFile createValidUploadedFile() {
    var uuid = UUID.randomUUID();
    var uploadedFile = new UploadedFile();
    var s3Key = "testKey";
    uploadedFile.setId(uuid);
    uploadedFile.setS3Key(s3Key);

    return uploadedFile;
  }

  private static MultipartFile createMultipartFile() throws IOException {
    var multipartFile = Mockito.mock(MultipartFile.class);
    fileSize = 123L;
    filename = "file.txt";
    contentType = "text";
    when(multipartFile.getSize()).thenReturn(fileSize);
    when(multipartFile.getContentType()).thenReturn(contentType);
    when(multipartFile.getOriginalFilename()).thenReturn(filename);
    when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));

    return multipartFile;
  }
}